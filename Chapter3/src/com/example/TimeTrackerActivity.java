package com.example;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class TimeTrackerActivity extends Activity implements OnClickListener {
    private TimeTask mTimeTask = null;
    private TimeListAdapter mTimeListAdapter = null;

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
    public void onClick(View v) {
        TextView ssButton = (TextView) findViewById(R.id.start_stop);

        if (v.getId() == R.id.start_stop) {
            if (mTimeTask == null) {
                // handle start
                mTimeTask = new TimeTask();
                mTimeTask.execute( (Void[])null );

                ssButton.setText(R.string.stop);
            } else if (mTimeTask.stopped == true) {
                mTimeTask.stopped = false;
                ssButton.setText(R.string.stop);
            } else {
                mTimeTask.stopped = true;
                ssButton.setText(R.string.start);
            }
        } else if (v.getId() == R.id.reset) {
            if (mTimeTask != null) {
                mTimeTask.stopped = true;
                mTimeTask.keepRunning = false;
            }
            TextView counter = (TextView) findViewById(R.id.counter);
            counter.setText(DateUtils.formatElapsedTime(0));
            ssButton.setText(R.string.start);
            mTimeTask = null;
        }
    }

    public class TimeTask extends AsyncTask<Void, Long, Long> {
        public boolean keepRunning = false;
        public boolean stopped = false;

        @Override
        protected Long doInBackground(Void... params) {
            keepRunning = true;
            long time = 0;
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
                    publishProgress(time/1000);
                }
            }
            return time/1000;
        }
        
        @Override
        protected void onProgressUpdate(Long... values) {
            TextView counter = (TextView) TimeTrackerActivity.this.findViewById(R.id.counter);
            counter.setText(DateUtils.formatElapsedTime(values[0]));
        }
        
        @Override
        protected void onPostExecute(Long result) {
            if (mTimeListAdapter != null)
                mTimeListAdapter.add(result);
        }
    }
}