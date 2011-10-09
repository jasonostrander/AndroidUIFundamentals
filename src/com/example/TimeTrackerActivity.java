package com.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class TimeTrackerActivity extends Activity implements OnClickListener {
    private TimeThread mTimeThread = null;
    private TimeListAdapter mTimeListAdapter = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Initialize the Timer
        TextView counter = (TextView) findViewById(R.id.counter);
        counter.setText(Utils.formatElapsedTime(0));

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
    public void onClick(View v) {
        TextView ssButton = (TextView) findViewById(R.id.start_stop);

        if (v.getId() == R.id.start_stop) {
            if (mTimeThread == null) {
                // handle start
                mTimeThread = new TimeThread();
                mTimeThread.start();

                ssButton.setText(R.string.stop);
            } else if (mTimeThread.stopped == true) {
                mTimeThread.stopped = false;
                ssButton.setText(R.string.stop);
            } else {
                mTimeThread.stopped = true;
                ssButton.setText(R.string.start);
            }
        } else if (v.getId() == R.id.reset) {
            // Handle stop
            if (mTimeThread != null) {
                mTimeThread.stopped = true;
                mTimeThread.running = false;
            }
            TextView counter = (TextView) findViewById(R.id.counter);
            counter.setText(Utils.formatElapsedTime(0));
            ssButton.setText(R.string.start);
            if (mTimeListAdapter != null && mTimeThread != null)
                mTimeListAdapter.add(new Time("Session 1", mTimeThread.time));
            mTimeThread = null;
        }
    }

    public class TimeThread extends Thread {
        public boolean running = false;
        public boolean stopped = false;
        private long time = 0;

        public void run() {
            final TextView counter = (TextView) TimeTrackerActivity.this.findViewById(R.id.counter);

            running = true;
            time = 0;
            long start = System.currentTimeMillis();
            while (running) {
                start = System.currentTimeMillis();
                
                // Sleep each iteration of the thread
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!stopped) {
                    runOnUiThread(new Thread() {
                        public void run() {
                            counter.setText(Utils.formatElapsedTime(time));
                        }
                    });
                    time += System.currentTimeMillis() - start;
                }
            }
        }
    }
}