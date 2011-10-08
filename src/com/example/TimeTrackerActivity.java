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
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Initialize the Timer
        TextView counter = (TextView) findViewById(R.id.counter);
        counter.setText(Utils.formatElapsedTime(0));

        Button startButton = (Button) findViewById(R.id.start);
        startButton.setOnClickListener(this);

        Button stopButton = (Button) findViewById(R.id.stop);
        stopButton.setOnClickListener(this);
        
        ListView list = (ListView) findViewById(R.id.time_list);
        list.setAdapter(new TimeListAdapter(this, 0));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start) {
            // handle start
            if (mTimeThread == null)
                mTimeThread = new TimeThread();
            mTimeThread.start();
        } else if (v.getId() == R.id.stop) {
            // Handle stop
            if (mTimeThread != null)
                mTimeThread.running = false;
            mTimeThread = null;
        }
    }
    
    public class TimeThread extends Thread {
        private boolean running = false;
        private long time = 0;
        
        public void run() {
            final TextView counter = (TextView) TimeTrackerActivity.this.findViewById(R.id.counter);
            
            if (counter != null) {
                running = true;
                time = 0;
                while (running) {
                    long start = System.currentTimeMillis();
                    runOnUiThread(new Thread() {
                        public void run() {
                            counter.setText(Utils.formatElapsedTime(time));
                        }
                    });
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    time += System.currentTimeMillis() - start;
                }
            }
        }
    }
}