package com.aoc.hn.hackernews;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aoc.hn.hackernews.obj.CommentItem;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.recyclerview.itemanimator.ScaleInOutItemAnimator;

/**
 * Created by aoc on 3/7/15.
 */
public class CommentsFragment extends Fragment {

    List<CommentItem> mComments = null;
    CommentsAdapter mAdapter = null;
    ProgressBar progressBar = null;
    RecyclerView mRecyclerView = null;

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
            RecyclerView.ItemDecoration itemDecoration = new CustomItemDecoration(getActivity());
            mRecyclerView.addItemDecoration(itemDecoration);
            mRecyclerView.setItemAnimator(new ScaleInOutItemAnimator(mRecyclerView));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_comments_list, container, false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.comments_list_view);
        return rootView;
    }

    public void addCommentItem(CommentItem co) {
        log("adding comment item");
        mComments.add(co);
        mAdapter.notifyItemInserted(mComments.size());
    }

    public void clear() {
        if(mComments == null || mAdapter == null) {
            return;
        }
        mComments.clear();
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

