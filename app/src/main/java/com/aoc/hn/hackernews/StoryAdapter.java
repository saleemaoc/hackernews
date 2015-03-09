package com.aoc.hn.hackernews;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aoc.hn.hackernews.models.StoryItem;

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

    /**
     *
     * @param parent
     * @param i
     * @return
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.story_list_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(rowView);
        return viewHolder;
    }

    /**
     * bind data at give position in the list, to ViewHolder object
     * @param holder
     * @param position
     */
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

    /**
     * ViewHolder object to hold all views for one story
     */
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title = null;
        public TextView info = null;
        public TextView openBtn = null;

        public MyViewHolder(final View rowView) {
            super(rowView);

            title = (TextView) rowView.findViewById(R.id.title);
            info = (TextView) rowView.findViewById(R.id.info);
            openBtn = (TextView) rowView.findViewById(R.id.openBtn);

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

        /**
         * when story item clicked, show its comments
         * @param view
         */
        @Override
        public void onClick(View view) {
            List<String> comments = mStories.get(getPosition()).comments;
            if(comments.size() <= 0) {
                Toast.makeText(mContext, "No comments for this story!", Toast.LENGTH_SHORT).show();
                return;
            }
            ((ListActivity) mContext).showCommentsFragment(comments);
        }
    }
}
