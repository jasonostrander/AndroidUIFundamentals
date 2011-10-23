package com.example;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class TimeTrackerActivity extends FragmentActivity implements OnClickListener, ServiceConnection {
    public static final String ACTION_TIME_UPDATE = "ActionTimeUpdate";
    public static final String ACTION_TIMER_FINISHED = "ActionTimerFinished";

    public static int TIMER_NOTIFICATION = 0;

    private TimeListAdapter mTimeListAdapter = null;
    
    private TimerService mTimerService = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v("jason", "Activity.onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Initialize the Timer
        TextView counter = (TextView) findViewById(R.id.counter);
        counter.setText(DateUtils.formatElapsedTime(0));

        Button startButton = (Button) findViewById(R.id.start_stop);
        startButton.setOnClickListener(this);

        Button finishButton = (Button) findViewById(R.id.finish);
        finishButton.setOnClickListener(this);

        if (mTimeListAdapter == null)
            mTimeListAdapter = new TimeListAdapter(this, 0);
        
        ListView list = (ListView) findViewById(R.id.time_list);
        list.setAdapter(mTimeListAdapter);
        
        // Register the TimeReceiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_TIME_UPDATE);
        filter.addAction(ACTION_TIMER_FINISHED);
        registerReceiver(mTimeReceiver, filter);
        
    }
    
    @Override
    protected void onResume() {
        Log.v("jason", "Activity.onResume");
        super.onResume();

        // Bind to the TimerService
        bindTimerService();
    }
    
    @Override
    protected void onDestroy() {
        if (mTimeReceiver != null)
            unregisterReceiver(mTimeReceiver);
        
        if (mTimerService != null) {
            unbindService(this);
            mTimerService.stop();
            mTimerService = null;
        }
    }
    
    @Override
    public void onClick(View v) {
        TextView ssButton = (TextView) findViewById(R.id.start_stop);

        if (v.getId() == R.id.start_stop) {
            if (mTimerService == null) {
                ssButton.setText(R.string.stop);
                startService(new Intent(this, TimerService.class));
            } else if (mTimerService.isStopped() == true) {
                ssButton.setText(R.string.stop);
                mTimerService.startService(new Intent(this, TimerService.class));
            } else {
                ssButton.setText(R.string.start);
                mTimerService.stop();
            }
        } else if (v.getId() == R.id.finish) {
            if (mTimerService != null) {
                mTimerService.reset();
            }
            TextView counter = (TextView) findViewById(R.id.counter);
            counter.setText(DateUtils.formatElapsedTime(0));
            ssButton.setText(R.string.start);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.clear_all:
            //Testing
            FragmentManager fm = getSupportFragmentManager();
            if (fm.findFragmentByTag("dialog") == null) {
                ConfirmClearDialogFragment frag = ConfirmClearDialogFragment.newInstance(mTimeListAdapter);
                frag.show(fm, "dialog");
            }
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    
    private void bindTimerService() {
        bindService(new Intent(this, TimerService.class), this, Context.BIND_AUTO_CREATE);
    }

    private BroadcastReceiver mTimeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_TIME_UPDATE.equals(action)) {
                long time = intent.getLongExtra("time", 0);
                TextView counter = (TextView) TimeTrackerActivity.this.findViewById(R.id.counter);
                counter.setText(DateUtils.formatElapsedTime(time/1000));
            } else if (ACTION_TIMER_FINISHED.equals(action)) {
                long time = intent.getLongExtra("time", 0);
                if (mTimeListAdapter != null)
                    mTimeListAdapter.add(time/1000);
            }
        }
    };
    
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.v("jason", "onServiceConnected");
        mTimerService = ((TimerService.LocalBinder)service).getService();
    }
    
    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.v("jason", "onServiceDisconnected");
        mTimerService = null;
    }
}

