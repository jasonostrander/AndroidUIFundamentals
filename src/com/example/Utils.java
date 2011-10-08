package com.example;

import android.content.res.Resources;

public class Utils {
    public static long ONE_HOUR = 1000*60*60;
    public static long ONE_MINUTE = 1000*60;
    public static long ONE_SECOND = 1000;

    public static String formatElapsedTime(long timeMillis) {
        long hours = 0;
        long minutes = 0;
        long seconds = 0;
        
        if (timeMillis > ONE_HOUR) {
            hours = timeMillis / ONE_HOUR;
            timeMillis -= ONE_HOUR*hours;
        }
        
        if (timeMillis > ONE_MINUTE) {
            minutes = timeMillis / ONE_MINUTE;
            timeMillis -= ONE_MINUTE*minutes;
        }

        if (timeMillis > ONE_SECOND) {
            seconds = timeMillis / ONE_SECOND;
            timeMillis -= ONE_SECOND*seconds;
        }

//        String format = Resources.getSystem().getString(R.string.elapsed_time_format);
        return String.format("%1$d:%2$02d:%3$02d:%4$03d", hours, minutes, seconds, timeMillis);
    }
}
