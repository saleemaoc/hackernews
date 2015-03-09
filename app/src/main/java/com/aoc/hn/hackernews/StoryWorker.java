


package com.aoc.hn.hackernews;

import android.os.AsyncTask;
import android.util.Log;

import com.aoc.hn.hackernews.db.StoryORM;
import com.aoc.hn.hackernews.models.StoryItem;
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
import java.util.Arrays;
import java.util.List;

public class StoryWorker extends AsyncTask<String, Integer, Boolean>{

    List<String> totalStories = null;
	StoryFragment storiesFragment = null;
	private boolean mCancel = false;
    private int counter = 0;
    private boolean forceRefresh = false;

    /**
     * Download IDs of all the top stories. Store them in a separate list. From that list, download actual stories with infinite scrolling.
     * @param f
     * @param forceRefresh whether its must fetch data from server, instead of from local db
     */
	public StoryWorker(StoryFragment f, boolean forceRefresh) {
        this.totalStories = new ArrayList<String>();
		this.storiesFragment = f;
        this.forceRefresh = forceRefresh;
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

            // get IDs of stories in a list
	        final GsonBuilder gsonBuilder = new GsonBuilder();
	        final Gson gson = gsonBuilder.create();
	        while (jsonReader.hasNext()) {
	        	totalStories.add((String) gson.fromJson(jsonReader, String.class));
	        }
	        jsonReader.endArray();
	        jsonReader.close();
	        return true;
		}catch(UnknownHostException uhe){
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
		// log(result + "");
		if(!result) {
            // failed to get the json data -- see if we already have previously stored IDs of stories, so the app can load those
            checkLocalStoryIDs();
            return;
        }
        // we have IDs of top stories here, this could be of use when there is no internet connection
        String ids = "";
        for(String id: totalStories) {
            ids += id + ":";
        }
        ids = ids.substring(0, ids.length() - 1);
        storiesFragment.saveStoryIDs(ids);
        // load more items
        loadMore(0, storiesFragment.visibleThreshold);
    }

    private void checkLocalStoryIDs() {
        totalStories = null;
        String s = storiesFragment.getStoryIDs();
        if(s != null) {
            totalStories = Arrays.asList(s.split(":"));
        }
        if(totalStories == null || totalStories.size() <= 0) {
            storiesFragment.noStoriesFound();
            storiesFragment.hideProgressBar();
            return;
        }
        loadMore(0, storiesFragment.visibleThreshold);
    }

    /**
     * Load items, either from server or local db, depending on <code>forceRefresh</code> with infinite scrolling. For every refresh,
     * it will load <code>end - start</code> more items from server
     * @param start start index of the IDs list
     * @param end end index of the IDs list.
     */
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
            if(!forceRefresh && getFromDB(id) != null) {
                continue;
            }
            StoryItemWorker cw = new StoryItemWorker();
            cw.execute(Constants.URL_ITEM_DETAILS + id + ".json");
        }
    }

    /**
     * get story data from local database, given its id
     * @param id id of the story to load data for, from local database
     * @return <code>StoryItem</code> object or null if there was no object of the given id in database
     */
    private StoryItem getFromDB(String id) {
        try {
            StoryItem si = StoryORM.findById(storiesFragment.getActivity(), Long.parseLong(id));
            if(si != null) {
                storiesFragment.addStoryItem(si);
                counter--;
                return si;
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * Check if this worker is already busy in downloading data
     * @return true if it is already busy in downloading data, false otherwise
     */
    public boolean isLoading() {
        return this.counter > 0;
    }

    public void log(String msg){
        if(Constants.DEBUG == false) {
            return;
        }
        Log.e(getClass().getName(), msg);
	}
	
	public void cancel() {
		this.mCancel = true;
	}

    /**
     * This class gets data from server for a single story object, given its ID.
     */
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