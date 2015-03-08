package com.aoc.hn.hackernews.obj;

import com.google.gson.annotations.SerializedName;

public class CommentReplyItem {
	
	@SerializedName("time")
	public long time = -1;
	
	@SerializedName("by")
	public String author = null;
	
	@SerializedName("text")
	public String content = null;
}
