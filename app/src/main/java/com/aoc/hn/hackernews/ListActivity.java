package com.aoc.hn.hackernews;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.aoc.hn.hackernews.obj.StoryItem;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends ActionBarActivity {

	public static String URL_TOP_STORIES = "https://hacker-news.firebaseio.com/v0/topstories.json";
	public static String URL_STORY_DETAILS = "https://hacker-news.firebaseio.com/v0/item/";

	private StoryFragment mFragment = null;
	private StoriesWorker mStoriesWorker = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		if (savedInstanceState == null) {
			mFragment = new StoryFragment();
			getSupportFragmentManager().beginTransaction().add(R.id.container, mFragment).commit();
		}
		mStoriesWorker = new StoriesWorker(mFragment);
		mStoriesWorker.execute(URL_TOP_STORIES);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 */

	@Override
	public void finish() {
		super.finish();
		mStoriesWorker.cancel();
	}
	
	public void log(String msg){
		Log.e(getClass().getName(), msg);
	}

}
