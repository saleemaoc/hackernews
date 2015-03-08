package com.aoc.hn.hackernews;

import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aoc.hn.hackernews.obj.CommentItem;
import com.aoc.hn.hackernews.obj.CommentReplyItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ReplyWorker extends AsyncTask<String, Integer, CommentReplyItem> {

	private boolean mCancel = false;
    private TextView replyContent = null, replyInfo = null;
    private ProgressBar progressBar = null;
    private CommentItem commentItem = null;

	public ReplyWorker(TextView rc, TextView ri, ProgressBar pb, CommentItem ci) {
        this.replyContent = rc;
        this.replyInfo = ri;
        this.progressBar = pb;
        commentItem = ci;
    }

	public void log(String msg){
		Log.e(getClass().getName(), msg + "");
	}
	
	public void cancel() {
		this.mCancel = true;
	}

    @Override
    protected CommentReplyItem doInBackground(String... params) {
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
            CommentReplyItem comment = gson.fromJson(sb.toString(), CommentReplyItem.class);
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
    protected void onPostExecute(CommentReplyItem replyItem) {
        super.onPostExecute(replyItem);
        this.progressBar.setVisibility(View.GONE);
        if(replyItem == null) {
            // failed to retrieve the reply item
            return;
        }
        commentItem.latestReply = replyItem.content;
        replyContent.setVisibility(View.VISIBLE);
        replyInfo.setVisibility(View.VISIBLE);
        replyContent.setText(Html.fromHtml(replyItem.content + ""));
        replyInfo.setText("Posted by " + replyItem.author + " " + Utils.durationFromUnixTime(replyItem.time));
    }
	
}