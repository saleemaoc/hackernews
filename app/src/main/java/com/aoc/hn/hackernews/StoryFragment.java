package com.aoc.hn.hackernews;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
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

import com.aoc.hn.hackernews.models.StoryItem;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.recyclerview.itemanimator.ScaleInOutItemAnimator;

/**
 * Created by aoc on 3/7/15.
 */
public class StoryFragment extends Fragment {

    List<StoryItem> mStories = null;
    StoryAdapter mAdapter = null;
    RecyclerView mRecyclerView = null;
    SwipeRefreshLayout mSwipeLayout = null;
    StoryWorker mStoriesWorker = null;

    private int previousTotal = 0;
    private boolean loading = true;
    public int visibleThreshold = 20;
    public int totalItemCount;

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

                    totalItemCount = mStoriesWorker.totalStories.size();
                    int totalLoadedItems = Math.max(mLayoutManager.getItemCount(), mStories.size());
                    int lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
                    if (loading) {
                        if (totalLoadedItems > previousTotal) {
                            loading = false;
                            previousTotal = totalLoadedItems;
                        }
                    }
                    // load more items if we have reached the end of the scroll
                    // log(lastVisibleItem + "; " + totalLoadedItems + "; " + totalItemCount+ "; " + loading);
                    if(!loading && (lastVisibleItem >= (totalLoadedItems - 1) && totalLoadedItems < totalItemCount)) {
                        if(mStoriesWorker.isLoading()) {
                           // log("already loading data.. ");
                            return;
                        }
                        // log("scroll end reached");
                        mSwipeLayout.setRefreshing(true);
                        loading = true;
                        mStoriesWorker.loadMore(lastVisibleItem + 1, lastVisibleItem + 1 + visibleThreshold);
                    }
                }
            });
        }

        fetchStories(false  );
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mStories.clear();
                mAdapter.notifyDataSetChanged();
                fetchStories(true);
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stories_list, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.stories_list_view);
        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeLayout);
        mSwipeLayout.setColorSchemeColors(getResources().getColor(R.color.color_foreground));
        mSwipeLayout.setProgressBackgroundColor(R.color.color_accent);
        return rootView;
    }

    /**
     * a story item received, show it in the list
     * @param sItem
     */
    public void addStoryItem(StoryItem sItem) {
//        log("adding story item");
        mSwipeLayout.setRefreshing(false);
        if(mStories.contains(sItem)) {
            return;
        }
        mStories.add(sItem);
        mAdapter.notifyItemInserted(mStories.size());
    }

    /**
     * retrievew story items
     * @param forceRefresh
     */
    public void fetchStories(final boolean forceRefresh) {
        Runnable refreshIndicator = new Runnable() {
            @Override
            public void run() {
                mSwipeLayout.setRefreshing(true);
                mStoriesWorker = new StoryWorker(StoryFragment.this, forceRefresh);
                mStoriesWorker.execute(Constants.URL_TOP_STORIES);
            }
        };
        if(mStories.size() <= 0) {
            new Handler().postDelayed(refreshIndicator, 600);
        }
    }

    public void hideProgressBar() {
        mSwipeLayout.setRefreshing(false);
    }

    // failed to get data from server
    public void noStoriesFound() {
        Toast.makeText(getActivity(), "No data found!", Toast.LENGTH_SHORT).show();
    }

    // save IDs for stories
    public boolean saveStoryIDs(String IDs) {
        SharedPreferences sp = getActivity().getSharedPreferences(Constants.PREFS_FILE_NAME, getActivity().MODE_PRIVATE);
        return sp.edit().putString(Constants.STORIES_IDs, IDs).commit();
    }

    // get IDs of stories if we have it already stored
    public String getStoryIDs() {
        SharedPreferences sp = getActivity().getSharedPreferences(Constants.PREFS_FILE_NAME, getActivity().MODE_PRIVATE);
        return sp.getString(Constants.STORIES_IDs, null);
    }

    public void log(String msg){
        if(Constants.DEBUG == false) {
            return;
        }
        Log.e(getClass().getName(), msg);
    }
}

