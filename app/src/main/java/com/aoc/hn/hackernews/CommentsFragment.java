package com.aoc.hn.hackernews;

import android.annotation.SuppressLint;
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

import com.aoc.hn.hackernews.models.CommentItem;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.recyclerview.itemanimator.ScaleInOutItemAnimator;

/**
 * Created by aoc on 3/7/15.
 */
public class CommentsFragment extends Fragment {

    List<CommentItem> mComments = null;
    CommentsAdapter mAdapter = null;
    RecyclerView mRecyclerView = null;
    SwipeRefreshLayout mSwipeLayout = null;
    List<String> commentIDs = null;
    CommentsWorker mCommentsWorker = null;

    public CommentsFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mComments = new ArrayList<CommentItem>();
        mAdapter = new CommentsAdapter(getActivity(), mComments);
        if(mRecyclerView != null) {
            mRecyclerView.setAdapter(mAdapter);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setItemAnimator(new ScaleInOutItemAnimator(mRecyclerView));
        }
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mComments.clear();
                mAdapter.notifyDataSetChanged();
                fetchComments(commentIDs, true);
            }
        });

    }

    /**
     * Called by StoryFragment to request to load comments for a selected story
     * @param commentIDs An array of IDs of comments for the selected story
     * @param forceRefresh whether it must get results from server and update local db. If there is
     *                     not internet connection, only then it will load from local db.
     */
    public void fetchComments(final List<String> commentIDs, final boolean forceRefresh) {
        if(commentIDs == null) {
            log("commentIDs is null");
            return;
        }
        this.commentIDs = commentIDs;

        Runnable refreshIndicator = new Runnable() {
            @Override
            public void run() {
                mSwipeLayout.setRefreshing(true);
                clear();
                mCommentsWorker = new CommentsWorker(CommentsFragment.this);
                mCommentsWorker.fetchComments(commentIDs, forceRefresh);
            }
        };
        if(mComments.size() <= 0) {
            new Handler().postDelayed(refreshIndicator, 400);
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_comments_list, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.comments_list_view);
        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeLayout);
        mSwipeLayout.setColorSchemeColors(getResources().getColor(R.color.color_foreground));
        mSwipeLayout.setProgressBackgroundColor(R.color.color_accent);
        return rootView;
    }

    /**
     * @param hidden
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden && mCommentsWorker != null){
            mCommentsWorker.cancel();
            mComments.clear();
        }
    }

    /**
     * A new comment is received from server. add it to adapter
     * @param co
     */
    public void addCommentItem(CommentItem co) {
//        log("adding comment item");
        if(co.content == null) {
            return;
        }
        mComments.add(co);
        /*
        // re order views based on time.. and keep the current item on screen (this could be problematic in reading, so not using)
        mRecyclerView.scrollToPosition(mComments.indexOf(co));
        Collections.sort(mComments, new Comparator<CommentItem>(){
            @Override
            public int compare(CommentItem commentItem, CommentItem commentItem2) {
                return (int) (commentItem2.time - commentItem.time);
            }
        });
        */
        mAdapter.notifyItemInserted(mComments.size());
    }

    /**
     * empty the comments list
     */
    public void clear() {
        if(mComments == null || mAdapter == null) {
            return;
        }
        mComments.clear();
        mAdapter.notifyDataSetChanged();
    }

    public void hideProgressBar() {
        mSwipeLayout.setRefreshing(false);
    }

    public void log(String msg){
        if(Constants.DEBUG == false) {
            return;
        }
        Log.e(getClass().getName(), msg + "");
    }

}

