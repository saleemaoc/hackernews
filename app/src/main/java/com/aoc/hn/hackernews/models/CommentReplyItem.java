package com.aoc.hn.hackernews.models;

import com.google.gson.annotations.SerializedName;

public class CommentReplyItem {

    @SerializedName("id")
    public long id = -1;

    @SerializedName("time")
	public long time = -1;
	
	@SerializedName("by")
	public String author = null;
	
	@SerializedName("text")
	public String content = null;
}
