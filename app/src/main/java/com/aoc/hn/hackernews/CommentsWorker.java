package com.aoc.hn.hackernews;

import android.os.AsyncTask;
import android.util.Log;

import com.aoc.hn.hackernews.db.CommentORM;
import com.aoc.hn.hackernews.models.CommentItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class CommentsWorker {

	CommentsFragment commentsFragment = null;
	private boolean mCancel = false;

	public CommentsWorker(CommentsFragment f) {
		this.commentsFragment = f;
	}

    /**
     *
     * @param commentIDs Array of IDs of comments to load
     * @param forceRefresh whether load from server or from local database
     */
    public void fetchComments(List<String> commentIDs, boolean forceRefresh) {
		for (String id : commentIDs) {
            // log("getting comment " + id);
            // if its not force refresh, load from local db, if we have it
            if(!forceRefresh && recordInDB(id)) {
                continue;
            }
            // otherwise download from server
            CommentItemWorker cw = new CommentItemWorker();
            cw.execute(Constants.URL_ITEM_DETAILS + id + ".json");
		}
	}

    /**
     * check if we already have the comment in local database
     * @param id id of the comment to load
     * @return comment object or null
     */
    private boolean recordInDB(String id) {
        try {
            CommentItem si = CommentORM.findById(commentsFragment.getActivity(), Long.parseLong(id));
            if(si != null) {
                commentsFragment.addCommentItem(si);
                commentsFragment.hideProgressBar();
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

	public void log(String msg){
		Log.e(getClass().getName(), msg + "");
	}

    /**
     * cancel any remaining tasks
     */
	public void cancel() {
		this.mCancel = true;
	}

    /**
     * Downloads data for a comment, including its latest reply, given its ID
     */
	public class CommentItemWorker extends AsyncTask<String, Integer, CommentItem>{

		@Override
		protected CommentItem doInBackground(String... params) {
            if(mCancel) {
                return null;
            }
			// log("requesting " + params[0]);
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
				// log(comment.content);
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
            // add comment to local database
            CommentORM.insert(commentsFragment.getActivity(), commentItem);
			// we have the story item to show in the list
			commentsFragment.addCommentItem(commentItem);
		}
	}

}