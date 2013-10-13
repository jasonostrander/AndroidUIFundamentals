package com.example;

import java.lang.ref.WeakReference;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class TimeTrackerActivity extends Activity implements OnClickListener {
    private TimeListAdapter mTimeListAdapter = null;
    private long mStart = 0;
    private long mTime = 0;
    private TimeHandler mHandler;

    /** Called when the activity is first created. */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Create a handler to run the timer
        mHandler = new TimeHandler(this);

        // Initialize the Timer
        TextView counter = (TextView) findViewById(R.id.counter);
        counter.setText(DateUtils.formatElapsedTime(0));

        Button startButton = (Button) findViewById(R.id.start_stop);
        startButton.setOnClickListener(this);

        Button stopButton = (Button) findViewById(R.id.reset);
        stopButton.setOnClickListener(this);

        if (mTimeListAdapter == null)
            mTimeListAdapter = new TimeListAdapter(this, 0);

        ListView list = (ListView) findViewById(R.id.time_list);
        list.setAdapter(mTimeListAdapter);

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

    @Override
    protected void onDestroy() {
        mHandler.removeMessages(0);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        TextView ssButton = (TextView) findViewById(R.id.start_stop);

        if (v.getId() == R.id.start_stop) {
            if (!isTimerRunning()) {
                startTimer();
                ssButton.setText(R.string.stop);
            } else {
                stopTimer();
                ssButton.setText(R.string.start);
            }
        } else if (v.getId() == R.id.reset) {
            resetTimer();
            TextView counter = (TextView) findViewById(R.id.counter);
            counter.setText(DateUtils.formatElapsedTime(0));
            ssButton.setText(R.string.start);
        }
    }

    private void startTimer() {
        mStart = System.currentTimeMillis();
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessage(0);
    }

    private void stopTimer() {
        mHandler.removeMessages(0);
    }

    private boolean isTimerRunning() {
        return mHandler.hasMessages(0);
    }

    private void resetTimer() {
        stopTimer();
        if (mTimeListAdapter != null)
            mTimeListAdapter.add(mTime/1000);

        mTime = 0;
    }

    private static class TimeHandler extends Handler {
        WeakReference<TimeTrackerActivity> mActivityRef;
        
        public TimeHandler(TimeTrackerActivity activity) {
            mActivityRef = new WeakReference<TimeTrackerActivity>(activity);
        }
        
        @Override
        public void handleMessage(Message msg) {
            TimeTrackerActivity activity = mActivityRef.get();
            if (activity != null) {
                long current = System.currentTimeMillis();
                activity.mTime += current - activity.mStart;
                activity.mStart = current;

                TextView counter = (TextView) activity.findViewById(R.id.counter);
                counter.setText(DateUtils.formatElapsedTime(activity.mTime/1000));

                sendEmptyMessageDelayed(0, 250);
            }
        }
    }
}