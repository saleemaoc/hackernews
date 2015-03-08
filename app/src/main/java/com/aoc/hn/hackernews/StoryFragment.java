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
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;

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
                    totalItemCount = mLayoutManager.getItemCount();
                    firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if (totalItemCount > previousTotal) {
                            loading = false;
                            previousTotal = totalItemCount;
                        }
                    }
                    if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                        // Do something
                        log("scroll");
                        mStoriesWorker.loadMore(currentPage * itemsPerPage, ++currentPage * itemsPerPage);
                        loading = true;
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

    int itemsPerPage = 10;
    public int currentPage = 1;

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

