package com.example;

import android.app.Application;
import android.os.StrictMode;

public class TimeTrackerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        
        if (Util.useStrictMode(this)) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
            .detectAll()
            .penaltyLog()
            .penaltyDialog()
            .build());
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
            .detectAll()
            .penaltyLog()
            .penaltyDialog()
            .build());
        }
    }
}
