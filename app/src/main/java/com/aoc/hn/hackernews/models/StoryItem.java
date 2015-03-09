package com.aoc.hn.hackernews.models;

import java.util.Iterator;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class StoryItem {
	
	public static int MAX_COMMENTS = 10;
	
	@SerializedName("id")
	public long id = -1;
	
	@SerializedName("title")
	public String title = null;
	
	@SerializedName("by")
	public String author = null;
	
	@SerializedName("score")
	public int points = -1;
	
	@SerializedName("time")
	public long time = -1;
	
	@SerializedName("url")
	public String url = null;
	
	@SerializedName("kids")
	public List<String> comments = null;
	
	public void truncateComments() {
		if(comments == null || comments.size() <= MAX_COMMENTS) {
			return;
		}
		comments = comments.subList(0, MAX_COMMENTS - 1);
        Iterator<String> i = comments.iterator();
        while(i.hasNext()) {
            if (i.next().length() <= 0) {
                i.remove();
            }
        }
	}

}
