package com.aoc.hn.hackernews.obj;

import com.google.gson.annotations.SerializedName;

public class CommentReply {
	
	@SerializedName("title")
	public String title = null;
	
	@SerializedName("by")
	public String author = null;
	
	@SerializedName("text")
	public String content = null;
}
