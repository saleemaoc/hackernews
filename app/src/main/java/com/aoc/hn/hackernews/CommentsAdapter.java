package com.aoc.hn.hackernews;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aoc.hn.hackernews.obj.CommentItem;

import java.util.List;

/**
 * Created by aoc on 3/7/15.
 */
public class CommentsAdapter extends ArrayAdapter<CommentItem> {
    private Activity mContext = null;
    private List<CommentItem> mComments = null;

    public CommentsAdapter(Activity context, List<CommentItem> stories) {
        super(context, R.layout.comment_list_item);
        this.mContext = context;
        this.mComments = stories;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse row view, if possible
        if (rowView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            rowView = inflater.inflate(R.layout.comment_list_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.content = (TextView) rowView.findViewById(R.id.content);
            viewHolder.info = (TextView) rowView.findViewById(R.id.info);
            rowView.setTag(viewHolder);
        }
        // set title and image url
        final ViewHolder holder = (ViewHolder) rowView.getTag();
        // TODO
        CommentItem ci = mComments.get(position);
        holder.content.setText(Html.fromHtml(ci.content));
        holder.info.setText(" by " + ci.author + "  " + Utils.durationFromUnixTime(ci.time));
        return rowView;
    }

    @Override
    public int getCount() {
        return this.mComments.size();
    }

    @Override
    public CommentItem getItem(int position) {
        return this.mComments.get(position);
    }

    class ViewHolder {
        public TextView content = null;
        public TextView info = null;
    }
}