package com.example;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;

import com.example.provider.TaskProvider;

public class TimerService extends Service {
    private static final String TAG = "TimerService";
    public static int TIMER_NOTIFICATION = 0;

    private NotificationManager mNM = null;
    private Notification mNotification = null;
    private long mStart = 0;
    private long mTime = 0;
    private long mTaskId = -1;

    public class LocalBinder extends Binder {
        TimerService getService() {
            return TimerService.this;
        }
    }
    private final IBinder mBinder = new LocalBinder();

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            long current = System.currentTimeMillis();
            mTime += current - mStart;
            mStart = current;
            
            updateTime(mTime);
            
            mHandler.sendEmptyMessageDelayed(0, 250);
        };
    };
    
    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + " with intent: " + intent + " mTaskId = " + mTaskId);
        
        if (isTimerRunning()) {
            stopTimer();
            return START_STICKY;
        }
        
        if (mTaskId < 0) {
            createNewTask();
        }

        // Show notification when we start the timer
        showNotification();

        mStart = System.currentTimeMillis();
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessage(0);
        
        // Keep restarting until we stop the service
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        // Cancel the ongoing notification.
        mNM.cancel(TIMER_NOTIFICATION);
        
        mHandler.removeMessages(0);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: " + intent);
        return mBinder;
    }

    public void stopTimer() {
        mHandler.removeMessages(0);
        stopSelf();
        mNM.cancel(TIMER_NOTIFICATION);
        
        updateTask();
        
        // Broadcast timer stopped
        Intent intent = new Intent(TimeTrackerActivity.ACTION_TIMER_STOPPED);
        intent.putExtra("time", mTime);
        sendBroadcast(intent);
    }

    public boolean isTimerRunning() {
        return mHandler.hasMessages(0);
    }

    public void resetTimer() {
        stopTimer();
        timerStopped(mTime);
        mTime = 0;
        mTaskId = -1;
    }

    /**
     * Shows the timer notification
     */
    private void showNotification() {
        mNotification = new Notification(R.drawable.icon, null, System.currentTimeMillis());
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;
        
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
    
    public Long getTaskId() {
        return this.mTaskId;
    }
    
    public void setTask(long id, long time) {
        resetTimer();
        mTaskId = id;
        mTime = time;
    }
    
    public long getTime() {
        return this.mTime;
    }
    
    public void setTime(long time) {
        mTime = time;
    }
    
    private void createNewTask() {
        AsyncQueryHandler handler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onInsertComplete(int token, Object cookie, Uri uri) {
                mTaskId = Long.parseLong(uri.getLastPathSegment());
            }
        };

        Uri uri = TaskProvider.getContentUri();
        ContentValues cv = new ContentValues();
        cv.put(TaskProvider.Task.NAME, getResources().getString(R.string.task_name));
        cv.put(TaskProvider.Task.DATE, System.currentTimeMillis());
        cv.put(TaskProvider.Task.ACTIVE, true);
        cv.put(TaskProvider.Task.TIME, mTime);

        handler.startInsert(0, null, uri, cv);
    }
    
    private void updateTask() {
        AsyncQueryHandler handler = new AsyncQueryHandler(getContentResolver()) {
        };

        Uri uri = TaskProvider.getContentUri();
        ContentValues cv = new ContentValues();
        cv.put(TaskProvider.Task.ACTIVE, false);
        cv.put(TaskProvider.Task.TIME, mTime);
        String where = TaskProvider.Task._ID + " = ?";
        String[] args = new String[] {Long.toString(mTaskId)};

        handler.startUpdate(0, null, uri, cv, where, args);
    }
}
