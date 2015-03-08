package com.aoc.hn.hackernews;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aoc.hn.hackernews.obj.StoryItem;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.recyclerview.itemanimator.ScaleInOutItemAnimator;

/**
 * Created by aoc on 3/7/15.
 */
public class StoryFragment extends Fragment {

    List<StoryItem> mStories = null;
    StoryAdapter mAdapter = null;
    ProgressBar progressBar = null;
    RecyclerView mRecyclerView = null;

    public StoryFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mStories = new ArrayList<StoryItem>();
        mAdapter = new StoryAdapter(getActivity(), mStories);
        if(mRecyclerView != null) {
            mRecyclerView.setAdapter(mAdapter);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);
            RecyclerView.ItemDecoration itemDecoration = new CustomItemDecoration(getActivity());
            mRecyclerView.addItemDecoration(itemDecoration);
            mRecyclerView.setItemAnimator(new ScaleInOutItemAnimator(mRecyclerView));
        }
    }

/*
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
//        Toast.makeText(getActivity(), mStories.get(position).comments + " comments", Toast.LENGTH_SHORT).show();
        ((ListActivity) getActivity()).showCommentsFragment(mStories.get(position).comments);
    }
*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stories_list, container, false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.stories_list_view);
        return rootView;
    }

    public void addStoryItem(StoryItem sItem) {
//        log("adding story item");
        mStories.add(sItem);
        mAdapter.notifyItemInserted(mStories.size());
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

