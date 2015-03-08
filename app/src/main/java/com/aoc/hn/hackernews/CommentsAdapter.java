package com.aoc.hn.hackernews;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.aoc.hn.hackernews.obj.CommentItem;

import java.util.List;

/**
 * Created by aoc on 3/7/15.
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder> {
    private Activity mContext = null;
    private List<CommentItem> mComments = null;

    public CommentsAdapter(Activity context, List<CommentItem> stories) {
        super();
        this.mContext = context;
        this.mComments = stories;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View rowView = LayoutInflater.from(mContext).inflate(R.layout.comment_list_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(rowView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CommentItem ci = mComments.get(position);
        holder.content.setText(Html.fromHtml(ci.content));
        holder.info.setText(" Posted by " + ci.author + " " + Utils.durationFromUnixTime(ci.time));
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView content = null;
        public TextView info = null;

        public MyViewHolder(View rowView) {
            super(rowView);

            rowView.setOnClickListener(this);
            content = (TextView) rowView.findViewById(R.id.content);
            info = (TextView) rowView.findViewById(R.id.info);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(mContext, mComments.get(getPosition()).latestReply, Toast.LENGTH_SHORT).show();
        }
    }
}