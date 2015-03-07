package com.aoc.hn.hackernews;

/**
 * Created by aoc on 3/7/15.
 */
public class Utils {

    public static String durationFromUnixTime(long creationTime) {
        long currentTime = System.currentTimeMillis()/1000L;
        long diff = currentTime - creationTime;
        String durationStr = "";
        if(diff < 60) {
            durationStr = diff + " seconds ago";
        } else if (diff < 3600) {
            durationStr = (long) Math.floor(diff/60) + " minutes ago ";
        } else if(diff < 86400) {
            durationStr = (long) Math.floor(diff/3600) + " hours ago";
        } else {
            durationStr = (long) Math.floor(diff/86400) + " days ago";
        }
        return durationStr;
    }
}
