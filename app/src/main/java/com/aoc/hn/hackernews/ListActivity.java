package com.aoc.hn.hackernews;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import java.util.List;

public class ListActivity extends ActionBarActivity {

	private StoryFragment mStoriesFragment = null;
	private StoryWorker mStoriesWorker = null;

    private CommentsFragment mCommentsFragment = null;
    private CommentsWorker mCommentsWorker = null;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(mStoriesFragment == null) {
            mStoriesFragment = (StoryFragment) getSupportFragmentManager().findFragmentById(R.id.stories_fragment);
        }
        if(mCommentsFragment == null) {
            mCommentsFragment = (CommentsFragment) getSupportFragmentManager().findFragmentById(R.id.comments_fragment);
        }
        getSupportFragmentManager().beginTransaction().hide(mCommentsFragment).commit();
    }

    public CommentsFragment getCommentsFragment() {
        return mCommentsFragment;
    }

    /**
     * show back the story list. this occurs when we have comments screen is displayed and back key is pressed
     */
    public void showStoriesFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.show(mStoriesFragment).hide(mCommentsFragment).commit();
    }

    @Override
    public void onBackPressed() {
        if(!mStoriesFragment.isVisible()) {
            showStoriesFragment();
            return;
        }
        super.onBackPressed();
    }

    /**
     * show Comments screen for selected story
     * @param commentIDs
     */
    public void showCommentsFragment(List<String> commentIDs) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.show(mCommentsFragment).hide(mStoriesFragment);
        transaction.commit();
        mCommentsFragment.fetchComments(commentIDs, false);
    }

    /**
	 */

	@Override
	public void finish() {
		super.finish();
	}
	
	public void log(String msg){
		Log.e(getClass().getName(), msg);
	}

}
