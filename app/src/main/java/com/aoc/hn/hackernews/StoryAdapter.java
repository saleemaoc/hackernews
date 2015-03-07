package com.aoc.hn.hackernews;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.aoc.hn.hackernews.obj.StoryItem;

import java.util.List;

/**
 * Created by aoc on 3/7/15.
 */
public class StoryAdapter extends ArrayAdapter<StoryItem> {
    private Activity mContext = null;
    private List<StoryItem> mStories = null;

    public StoryAdapter(Activity context, List<StoryItem> stories) {
        super(context, R.layout.story_list_item);
        this.mContext = context;
        this.mStories = stories;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse row view, if possible
        if (rowView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            rowView = inflater.inflate(R.layout.story_list_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.title = (TextView) rowView.findViewById(R.id.title);
            viewHolder.info = (TextView) rowView.findViewById(R.id.info);
            viewHolder.openBtn = (Button) rowView.findViewById(R.id.openBtn);
            rowView.setTag(viewHolder);
        }
        // set title and image url
        final ViewHolder holder = (ViewHolder) rowView.getTag();
        final StoryItem si = mStories.get(position);
        holder.title.setText(si.title);
        String infoStr = si.points + " points by " + si.author + "  " + Utils.durationFromUnixTime(si.time);
        holder.info.setText(infoStr);
        holder.openBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(mStories.get(position).url));
                mContext.startActivity(i);
            }
        });
        return rowView;
    }

    @Override
    public int getCount() {
        return this.mStories.size();
    }

    @Override
    public StoryItem getItem(int position) {
        return this.mStories.get(position);
    }

    class ViewHolder {
        public TextView title = null;
        public TextView info = null;
        public Button openBtn = null;
    }
}