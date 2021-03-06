package com.aoc.hn.hackernews;

/**
 * Created by aoc on 3/7/15.
 */
public class Utils {

    /**
     * get a pretty string for difference between current time and give unix time
     * @param creationTime unix time to create difference of, with respect to current time
     * @return string like '10 seconds ago', '1 hour ago', '2 days ago' etc.
     */
    public static String durationFromUnixTime(long creationTime) {
        long currentTime = System.currentTimeMillis()/1000L;
        long diff = currentTime - creationTime;
        String durationStr = "";
        if(diff < 60) {
            durationStr = diff + " seconds ago";
        } else if (diff < 3600) {
            long d = (long) Math.floor(diff/60);
            durationStr =  d == 1 ? d + " minute ago " : d + " minutes ago ";
        } else if(diff < 86400) {
            long d = (long) Math.floor(diff/3600);
            durationStr =  d == 1 ? d + " hour ago " : d + " hours ago ";
        } else {
            long d = (long) Math.floor(diff/86400);
            durationStr =  d == 1 ? d + " day ago " : d + " days ago ";
        }
        return durationStr;
    }
}
