package com.example;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
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
            if (isTimerStopped()) {
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
    
    private boolean isTimerStopped() {
        return !mHandler.hasMessages(0);
    }

    private void resetTimer() {
        stopTimer();
        if (mTimeListAdapter != null)
            mTimeListAdapter.add(mTime/1000);
        
        mTime = 0;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            long current = System.currentTimeMillis();
            mTime += current - mStart;
            mStart = current;
            
            TextView counter = (TextView) TimeTrackerActivity.this.findViewById(R.id.counter);
            counter.setText(DateUtils.formatElapsedTime(mTime/1000));
            
            mHandler.sendEmptyMessageDelayed(0, 100);
        };
    };
}