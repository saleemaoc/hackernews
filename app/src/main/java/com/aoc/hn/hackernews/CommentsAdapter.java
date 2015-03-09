package com.aoc.hn.hackernews;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aoc.hn.hackernews.models.CommentItem;

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
        if(ci.latestReply.length() <= 0) {
            holder.replyContent.setVisibility(View.GONE);
            holder.replyInfo.setVisibility(View.GONE);
        }
        if(ci.latestReplyId.length() > 0) {
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.replyInfo.setText("");
            holder.replyContent.setText("");
            ReplyWorker rw = new ReplyWorker(holder.replyContent, holder.replyInfo, holder.progressBar, ci);
            rw.execute(ListActivity.URL_ITEM_DETAILS + ci.latestReplyId + ".json");
        }
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView content = null;
        public TextView info = null;
        public TextView replyInfo = null;
        public TextView replyContent = null;
        public ProgressBar progressBar = null;

        public MyViewHolder(View rowView) {
            super(rowView);
            this.progressBar = (ProgressBar) rowView.findViewById(R.id.progressBar);
            rowView.setOnClickListener(this);
            content = (TextView) rowView.findViewById(R.id.content);
            info = (TextView) rowView.findViewById(R.id.info);
            replyInfo = (TextView) rowView.findViewById(R.id.replyInfo);
            replyContent = (TextView) rowView.findViewById(R.id.replyContent);
        }

        public void makeInvisible() {
            this.progressBar.setVisibility(View.GONE);
            this.info.setVisibility(View.GONE);
            this.content.setVisibility(View.GONE);
            this.replyContent.setVisibility(View.GONE);
            this.replyInfo.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View view) {
            // Toast.makeText(mContext, mComments.get(getPosition()).latestReplyId, Toast.LENGTH_SHORT).show();
        }
    }
}