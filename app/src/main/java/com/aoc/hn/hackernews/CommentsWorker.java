package com.aoc.hn.hackernews;

import android.os.AsyncTask;
import android.util.Log;

import com.aoc.hn.hackernews.obj.CommentItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class CommentsWorker {

	CommentsFragment commentsFragment = null;
	private boolean mCancel = false;

	public CommentsWorker(CommentsFragment f) {
		this.commentsFragment = f;
	}

    public void fetchComments(List<String> commentIDs) {
		for (String id : commentIDs) {
            log("getting comment " + id);
            CommentItemWorker cw = new CommentItemWorker();
            cw.execute(ListActivity.URL_ITEM_DETAILS + id + ".json");
		}
	}

	public void log(String msg){
		Log.e(getClass().getName(), msg + "");
	}
	
	public void cancel() {
		this.mCancel = true;
	}
	
	public class CommentItemWorker extends AsyncTask<String, Integer, CommentItem>{

		@Override
		protected CommentItem doInBackground(String... params) {
            if(mCancel) {
                return null;
            }
			log("requesting " + params[0]);
			StringBuilder sb = new StringBuilder();
			try {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(params[0]);
				
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity entity = httpResponse.getEntity();
				
				InputStream stream = entity.getContent();
		        int b;
		        while ((b = stream.read()) != -1) {
		            sb.append((char) b);
		        }
		        final GsonBuilder gsonBuilder = new GsonBuilder();
		        final Gson gson = gsonBuilder.create();
				CommentItem comment = gson.fromJson(sb.toString(), CommentItem.class);
                comment.setLatestReply();
				log(comment.content);
				return comment;
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(CommentItem commentItem) {
			super.onPostExecute(commentItem);
			commentsFragment.hideProgressBar();
			if(commentItem == null) {
				// failed to retrieve the story item
				return;
			}
			// we have the story item to show in the list
			commentsFragment.addCommentItem(commentItem);
		}
	}
	
}