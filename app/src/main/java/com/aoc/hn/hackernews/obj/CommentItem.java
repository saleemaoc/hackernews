package com.aoc.hn.hackernews.obj;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CommentItem {
	@SerializedName("id")
	public long id = -1;
	
	@SerializedName("text")
	public String content = null;
	
	@SerializedName("by")
	public String author = null;
	
	@SerializedName("time")
	public long time = -1;

    @SerializedName("kids")
    public List<String> kids = null;

    public String latestReplyId = "";

    public String latestReply = "";

    public void setLatestReply() {
        if(kids == null || kids.size() < 1) {
            return;
        }
        latestReplyId = kids.get(0);
        kids.clear();
    }
}
