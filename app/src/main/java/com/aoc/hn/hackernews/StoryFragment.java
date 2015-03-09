package com.aoc.hn.hackernews;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aoc.hn.hackernews.db.StoryORM;
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
    SwipeRefreshLayout mSwipeLayout = null;
    StoriesWorker mStoriesWorker = null;

    private int previousTotal = 0;
    private boolean loading = true;
    public int visibleThreshold = 20;
    public int firstVisibleItem, visibleItemCount, totalItemCount;

    public StoryFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mStories = new ArrayList<StoryItem>();
        mAdapter = new StoryAdapter(getActivity(), mStories);
        if(mRecyclerView != null) {
            mRecyclerView.setAdapter(mAdapter);
            final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setItemAnimator(new ScaleInOutItemAnimator(mRecyclerView));
            mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    visibleItemCount = mRecyclerView.getChildCount();
                    totalItemCount = mStoriesWorker.totalStories.size();
                    firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
                    int totalLoadedItems = Math.max(mLayoutManager.getItemCount(), mStories.size());
                    int lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();

                    if (loading) {
                        if (totalLoadedItems > previousTotal) {
                            loading = false;
                            previousTotal = totalLoadedItems;
                        }
                    }
                    // log(lastVisibleItem + "; " + totalLoadedItems + "; " + totalItemCount+ "; " + loading);
                    if(!loading && (lastVisibleItem >= (totalLoadedItems - 1) && totalLoadedItems < totalItemCount)) {
                        if(mStoriesWorker.isLoading()) {
                           log("already loading data.. ");
                            return;
                        }
                        log("scroll reloaded");
                        mSwipeLayout.setRefreshing(true);
                        loading = true;
                        mStoriesWorker.loadMore(lastVisibleItem + 1, lastVisibleItem + 1 + visibleThreshold);
                    }
                }
            });
        }

        fetchStories();
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mStories.clear();
                mAdapter.notifyDataSetChanged();
                fetchStories();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stories_list, container, false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.stories_list_view);
        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeLayout);
        return rootView;
    }

    public void addStoryItem(StoryItem sItem) {
//        log("adding story item");
        mSwipeLayout.setRefreshing(false);
        if(mStories.contains(sItem)) {
            log("already exists");
            return;
        }
        mStories.add(sItem);
        mAdapter.notifyItemInserted(mStories.size());
    }

    Runnable refreshIndicator = new Runnable() {
        @Override
        public void run() {
            mSwipeLayout.setRefreshing(true);
        }
    };

    public void fetchStories() {
        if(mStories.size() <= 0) {
            new Handler().postDelayed(refreshIndicator, 600);
        }
        mStoriesWorker = new StoriesWorker(StoryFragment.this);
        mStoriesWorker.execute(ListActivity.URL_TOP_STORIES);
    }

    public void hideProgressBar() {
        mSwipeLayout.setRefreshing(false);
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

