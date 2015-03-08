package com.aoc.hn.hackernews;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
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
public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.MyViewHolder> {
    private Activity mContext = null;
    private List<StoryItem> mStories = null;

    public StoryAdapter(Activity context, List<StoryItem> stories) {
        super();
        this.mContext = context;
        this.mStories = stories;
    }

/*    @Override
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
    }*/

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.story_list_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(rowView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final StoryItem si = mStories.get(position);
        holder.title.setText(si.title);
        String infoStr = si.points + " points. Posted by " + si.author + " " + Utils.durationFromUnixTime(si.time);
        holder.info.setText(infoStr);
    }

    @Override
    public int getItemCount() {
        return this.mStories.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView title = null;
        public TextView info = null;
        public Button openBtn = null;

        public MyViewHolder(View rowView) {
            super(rowView);

            title = (TextView) rowView.findViewById(R.id.title);
            info = (TextView) rowView.findViewById(R.id.info);
            openBtn = (Button) rowView.findViewById(R.id.openBtn);

            rowView.setOnClickListener(this);

            openBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(mStories.get(getPosition()).url));
                    mContext.startActivity(i);
                }
            });
        }

        @Override
        public void onClick(View view) {
            ((ListActivity) mContext).showCommentsFragment(mStories.get(getPosition()).comments);
        }
    }
}