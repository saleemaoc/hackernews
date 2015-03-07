package com.aoc.hn.hackernews;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aoc.hn.hackernews.obj.StoryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aoc on 3/7/15.
 */
public class StoryFragment extends ListFragment {

    List<StoryItem> mStories = null;
    StoryAdapter mAdapter = null;
    ProgressBar progressBar = null;

    public StoryFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mStories = new ArrayList<StoryItem>();
        mAdapter = new StoryAdapter(getActivity(), mStories);
        setListAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        return rootView;
    }

    public void addStoryItem(StoryItem sItem) {
//        log("adding story item");
        mStories.add(sItem);
        mAdapter.notifyDataSetChanged();
    }

    public void hideProgressBar() {
        if(progressBar != null)
           progressBar.setVisibility(View.GONE);
    }

    public void noStoriesFound() {
        Toast.makeText(getActivity(), "No data found!", Toast.LENGTH_SHORT).show();
    }

    public void log(String msg){
        Log.e(getClass().getName(), msg);
    }

}

