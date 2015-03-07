package com.aoc.hn.hackernews;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class ListActivity extends ActionBarActivity {

	public static String URL_TOP_STORIES = "https://hacker-news.firebaseio.com/v0/topstories.json";
	public static String URL_ITEM_DETAILS = "https://hacker-news.firebaseio.com/v0/item/";

	private StoryFragment mStoriesFragment = null;
	private StoriesWorker mStoriesWorker = null;

    private CommentsFragment mCommentsFragment = null;
    private CommentsWorker mCommentsWorker = null;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		if (savedInstanceState == null) {
			mStoriesFragment = new StoryFragment();
            mCommentsFragment = new CommentsFragment();
			transaction.add(R.id.container, mStoriesFragment).commit();
		}
		mStoriesWorker = new StoriesWorker(mStoriesFragment);
		mStoriesWorker.execute(URL_TOP_STORIES);
	}

    public CommentsFragment getCommentsFragment() {
        return mCommentsFragment;
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

    public void showStoriesFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, mStoriesFragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        // TODO fix fragment navigation
        super.onBackPressed();
    }

    public void showCommentsFragment(List<String> commentIDs) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, mCommentsFragment);
//        transaction.addToBackStack(null);
        transaction.commit();

        mCommentsFragment.clear();
        CommentsWorker mCommentsWorker = new CommentsWorker(mCommentsFragment);
        mCommentsWorker.fetchComments(commentIDs);
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
