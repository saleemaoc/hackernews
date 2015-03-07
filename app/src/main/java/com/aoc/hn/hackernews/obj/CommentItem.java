package com.aoc.hn.hackernews.obj;

import com.google.gson.annotations.SerializedName;

public class CommentItem {
	@SerializedName("id")
	public long id = -1;
	
	@SerializedName("text")
	public String content = null;
	
	@SerializedName("by")
	public String author = null;
	
	@SerializedName("time")
	public long time = -1;
}
