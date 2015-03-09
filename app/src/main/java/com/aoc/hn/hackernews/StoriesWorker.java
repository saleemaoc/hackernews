package com.aoc.hn.hackernews;

import android.os.AsyncTask;
import android.util.Log;

import com.aoc.hn.hackernews.db.StoryORM;
import com.aoc.hn.hackernews.obj.StoryItem;
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

public class StoriesWorker extends AsyncTask<String, Integer, Boolean>{

//	List<String> stories = null;
    List<String> totalStories = null;
	StoryFragment storiesFragment = null;
	private boolean mCancel = false;
    private int counter = 0;

	public StoriesWorker(StoryFragment f) {
//		this.stories = new ArrayList<String>();
        this.totalStories = new ArrayList<String>();
		this.storiesFragment = f;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		InputStream inputStream = null;
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(params[0]);

			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			inputStream = httpEntity.getContent();

			// use streaming to avoid loading the entire model in memory
	        JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));
	        jsonReader.beginArray();

	        // Type type = new TypeToken<List<String>>(){}.getType();
	        final GsonBuilder gsonBuilder = new GsonBuilder();
	        final Gson gson = gsonBuilder.create();
	        while (jsonReader.hasNext()) {
	        	totalStories.add((String) gson.fromJson(jsonReader, String.class));
//	        	log(stories.toString());
	        }
	        jsonReader.endArray();
	        jsonReader.close();
	        return true;
		}catch(UnknownHostException uhe){
			log("Unknown host exception");
			uhe.printStackTrace();
		}catch(Exception e) {
			e.printStackTrace();
		}
        return false;
    }

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		log(result+ "");
		if(!result) {
			// failed to get the json data
			storiesFragment.noStoriesFound();
            storiesFragment.hideProgressBar();
			return;
		}
		// TODO
        loadMore(0, storiesFragment.visibleThreshold);
    }

    public void loadMore(int start, int end) {
        counter = end - start;
        if(end >= totalStories.size()) {
            end = totalStories.size();
        }
        if(start >= end) {
            log("returning");
            return;
        }
        for (int i = start; i < end; i++) {
            String id = totalStories.get(i);
            try {
                StoryItem si = StoryORM.findById(storiesFragment.getActivity(), Long.parseLong(id));
                if(si != null) {
                    storiesFragment.addStoryItem(si);
                    counter--;
                    continue;
                }
            } catch (Exception e) {
                continue;
            }
            StoryItemWorker cw = new StoryItemWorker();
            cw.execute(ListActivity.URL_ITEM_DETAILS + id + ".json");
        }
    }

    public boolean isLoading() {
        return this.counter > 0;
    }

    public void log(String msg){
		Log.e(getClass().getName(), msg);
	}
	
	public void cancel() {
		this.mCancel = true;
	}
	
	public class StoryItemWorker extends AsyncTask<String, Integer, StoryItem>{

		@Override
		protected StoryItem doInBackground(String... params) {
            if(mCancel) {
                return null;
            }
//			log("requesting " + params[0]);
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
				StoryItem sItem = gson.fromJson(sb.toString(), StoryItem.class);
				sItem.truncateComments();
				log(sItem.id + " : " + sItem.title);
				return sItem;
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(StoryItem storyItem) {
			super.onPostExecute(storyItem);
            counter--;
			if(storyItem == null) {
				// failed to retrieve the story item
				return;
			}
            StoryORM.insert(storiesFragment.getActivity(), storyItem);
			// we have the story item to show in the list
			storiesFragment.addStoryItem(storyItem);
		}
	}
	
}