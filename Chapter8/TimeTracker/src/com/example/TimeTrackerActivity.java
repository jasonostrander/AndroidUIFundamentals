package com.example;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.TaskListFragment.TaskListener;

public class TimeTrackerActivity extends FragmentActivity 
        implements OnClickListener, ServiceConnection, 
        ViewPager.OnPageChangeListener, TaskListener {
    
    public static final String ACTION_TIME_UPDATE = "com.example.ActionTimeUpdate";
    public static final String ACTION_TIMER_FINISHED = "com.example.ActionTimerFinished";
    public static final String ACTION_TIMER_STOPPED = "com.example.ActionTimerStopped";
    private static final String TAG = "TimeTrackerActivity";
    public static int TIMER_NOTIFICATION = 0;
    public static int DATE_FLAGS = DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_DATE;

    private TimerService mTimerService = null;
    private TabHost mTabHost;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private long mCurrentTask = -1;
    private long mCurrentTime = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        if (savedInstanceState != null) {
            long id = savedInstanceState.getLong("id");
            if (id > 0)
                mCurrentTask = id;
            long time = savedInstanceState.getLong("time");
            if (time > 0)
                mCurrentTime = time;
        }
        
        FragmentManager fm = getSupportFragmentManager();
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new PagerAdapter(fm);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(this);
        
        // add tabs. Use ActionBar for 3.0 and above, otherwise use TabWidget
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final ActionBar bar = getActionBar();
            bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            bar.addTab(bar.newTab()
                    .setText(R.string.timer)
                    .setTabListener(new ABTabListener(mPager)));
            bar.addTab(bar.newTab()
                    .setText(R.string.tasks)
                    .setTabListener(new ABTabListener(mPager)));
        } else {
            // Use TabWidget instead
            mTabHost = (TabHost) findViewById(android.R.id.tabhost);
            mTabHost.setup();
            mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
                @Override
                public void onTabChanged(String tabId) {
                    if ("timer".equals(tabId)) {
                        mPager.setCurrentItem(0);
                    } else if ("tasks".equals(tabId)) {
                        mPager.setCurrentItem(1);
                    }
                }
            });

            String timer = getResources().getString(R.string.timer);
            mTabHost.addTab(mTabHost.newTabSpec("timer").setIndicator(timer).setContent(new DummyTabFactory(this)));
            String tasks = getResources().getString(R.string.tasks);
            mTabHost.addTab(mTabHost.newTabSpec("tasks").setIndicator(tasks).setContent(new DummyTabFactory(this)));
        }

        // Register the TimeReceiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_TIME_UPDATE);
        registerReceiver(mTimeReceiver, filter);
    }
    
    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();

        // Bind to the TimerService
        bindTimerService();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong("id", mCurrentTask);
        outState.putLong("time", mCurrentTime);
        super.onSaveInstanceState(outState);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
    }
    
    @Override
    protected void onDestroy() {
        if (mTimeReceiver != null)
            unregisterReceiver(mTimeReceiver);
        
        if (mTimerService != null) {
            mCurrentTask = mTimerService.getTaskId();
            mCurrentTime = mTimerService.getTime();
            unbindService(this);
            mTimerService = null;
        }
        super.onDestroy();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
            long taskId = data.getLongExtra(EditTaskActivity.TASK_ID, 0);
            long time = data.getLongExtra(EditTaskActivity.TASK_TIME, 0);
            String name = data.getStringExtra(EditTaskActivity.TASK_NAME);
            long date = data.getLongExtra(EditTaskActivity.TASK_DATE, 0);
            String desc = data.getStringExtra(EditTaskActivity.TASK_DESCRIPTION);
            
            if (taskId > -1) {
                onTaskSelected(taskId, name, desc, date, time);
            }
        }
    }
    
    @Override
    public void onClick(View v) {
        TextView ssButton = (TextView) findViewById(R.id.start_stop);

        if (v.getId() == R.id.start_stop) {
            if (mTimerService == null) {
                ssButton.setText(R.string.stop);
                startService(new Intent(this, TimerService.class));
            } else if (!mTimerService.isTimerRunning()) {
                ssButton.setText(R.string.stop);
                mTimerService.setTask(mCurrentTask, mCurrentTime);
                mTimerService.startService(new Intent(this, TimerService.class));
            } else {
                mCurrentTask = mTimerService.getTaskId();
                mCurrentTime = mTimerService.getTime();
                ssButton.setText(R.string.start);
                mTimerService.stopTimer();
            }
        } else if (v.getId() == R.id.edit) {
            // Finish the time input activity
            Intent intent = new Intent(TimeTrackerActivity.this, EditTaskActivity.class);
            intent.putExtra(EditTaskActivity.TASK_ID, mTimerService.getTaskId());
            intent.putExtra(EditTaskActivity.TASK_TIME, mTimerService.getTime());
            startActivityForResult(intent, 0);
        } else if (v.getId() == R.id.new_task) {
            startNewTimerTask();
            ssButton.setText(R.string.start);
        }
    }
    
    private void startNewTimerTask() {
        mPager.setCurrentItem(0);
        mTimerService.resetTimer();
        
        Resources res = getResources();
        onTaskSelected(
                -1,
                res.getString(R.string.new_task),
                res.getString(R.string.description),
                System.currentTimeMillis(),
                0);
    }
    
    private void bindTimerService() {
        bindService(new Intent(this, TimerService.class), this, Context.BIND_AUTO_CREATE);
    }

    private BroadcastReceiver mTimeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            long time = intent.getLongExtra("time", 0);

            if (ACTION_TIME_UPDATE.equals(action)) {
                TextView counter = (TextView) TimeTrackerActivity.this.findViewById(R.id.counter);
                if (counter != null)
                    counter.setText(DateUtils.formatElapsedTime(time/1000));
            }
        }
    };
    
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.i(TAG, "onServiceConnected");
        mTimerService = ((TimerService.LocalBinder)service).getService();
    }
    
    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.i(TAG, "onServiceDisconnected");
        mTimerService = null;
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int index) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final ActionBar bar = getActionBar();
            bar.setSelectedNavigationItem(index);
        } else {
            mTabHost.setCurrentTab(index);
        }
    }

    @Override
    public void onTaskSelected(long id, String name, String desc, long date, long time) {
        mPager.setCurrentItem(0);
        // ViewPager keeps fragments by tag: "android:switcher:<pager_id>:<item_pos>"
        TimerFragment frag = (TimerFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.pager+":0");
        frag.setName(name);
        frag.setDescription(desc);
        frag.setDate(DateUtils.formatDateTime(this, date, DATE_FLAGS));
        frag.setCounter(DateUtils.formatElapsedTime(time/1000));
        mCurrentTask = id;
        mCurrentTime = time;
        if (mTimerService != null)
            mTimerService.setTask(id, time);
    }
}

