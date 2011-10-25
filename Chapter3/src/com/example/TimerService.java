package com.example;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.format.DateUtils;
import android.util.Log;

public class TimerService extends Service {
    public static int TIMER_NOTIFICATION = 0;

    private NotificationManager mNM;
    private Notification mNotification;
    
    public class LocalBinder extends Binder {
        TimerService getService() {
            return TimerService.this;
        }
    }
    private final IBinder mBinder = new LocalBinder();

    private class TimerThread extends Thread {
        public boolean keepRunning = false;
        public boolean stopped = false;
        public long time = 0;

        @Override
        public void run() {
            keepRunning = true;
            long start = 0;
            while (keepRunning) {
                start = System.currentTimeMillis();
                
                // Sleep each iteration of the thread
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!stopped) {
                    time += System.currentTimeMillis() - start;
                    updateTime(time);
                }
            }
            
            timerStopped(time);
        }
    }
    private TimerThread mTimerThread = null;
    
    @Override
    public void onCreate() {
        Log.v("jason", "onCreate");
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("jason", "Received start id " + startId + ": " + intent);

        // Show notification when we start the timer
        showNotification();
        
        // start thread to handle updates
        if (mTimerThread == null) {
            mTimerThread = new TimerThread();
            mTimerThread.start();
        } else
            mTimerThread.stopped = false;

        // Keep restarting until we stop the service
        return START_STICKY;
    }
    
    @Override
    public void onDestroy() {
        Log.v("jason", "TS.onDestroy");
        // Cancel the ongoing notification.
        mNM.cancel(TIMER_NOTIFICATION);
        
        if (mTimerThread != null) {
            mTimerThread.keepRunning = false;
            mTimerThread = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v("jason", "onBind");
        return mBinder;
    }
    
    public void stop() {
        if (mTimerThread != null)
            mTimerThread.stopped = true;
        
        stopSelf();
        mNM.cancel(TIMER_NOTIFICATION);
    }
    
    public boolean isStopped() {
        if (mTimerThread == null)
            return true;
        return mTimerThread.stopped;
    }

    public void reset() {
        if (mTimerThread != null) {
            mTimerThread.stopped = true;
            mTimerThread.keepRunning = false;
            mTimerThread = null;
        }
    }
    
    /**
     * Shows the timer notification
     */
    private void showNotification() {
        mNotification = new Notification(R.drawable.icon, null, System.currentTimeMillis());
        // Use start foreground as user would notice if timer was stopped
        startForeground(TIMER_NOTIFICATION, mNotification);
    }

    /**
     * Update an existing notification.
     * 
     * @param time Time in milliseconds.
     */
    private void updateNotification(long time) {
        String title = getResources().getString(R.string.running_timer_notification_title);
        String message = DateUtils.formatElapsedTime(time/1000);

        Context context = getApplicationContext();
        Intent intent = new Intent(context, TimeTrackerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        mNotification.setLatestEventInfo(context, title, message, pendingIntent);
        mNM.notify(TIMER_NOTIFICATION, mNotification);
    }

    private void timerStopped(long time) {
        // Broadcast timer stopped
        Intent intent = new Intent(TimeTrackerActivity.ACTION_TIMER_FINISHED);
        intent.putExtra("time", time);
        sendBroadcast(intent);
        
        // Stop the notification
        stopForeground(true);
    }
    
    private void updateTime(long time) {
        // Broadcast the new time
        Intent intent = new Intent(TimeTrackerActivity.ACTION_TIME_UPDATE);
        intent.putExtra("time", time);
        sendBroadcast(intent);
        
        // Now update the notification
        updateNotification(time);
    }
}
