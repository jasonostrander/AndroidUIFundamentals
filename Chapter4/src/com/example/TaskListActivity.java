package com.example;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class TaskListActivity extends FragmentActivity {

    private TimeListAdapter mTimeListAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_list);
        
        TextView projectName = (TextView) findViewById(R.id.project_name);
        // set project name here
        
        Button newTask = (Button) findViewById(R.id.new_task);
        newTask.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskListActivity.this, TimeTrackerActivity.class);
                startActivity(intent);
            }
        });
        
        List<Long> values = new ArrayList<Long>();
        if (savedInstanceState != null) {
            long[] arr = savedInstanceState.getLongArray("times");
            for (long l : arr) {
                values.add(l);
            }
        }
        
        mTimeListAdapter = new TimeListAdapter(this, 0, values);
//        setListAdapter(mTimeListAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mTimeListAdapter != null) {
            int count = mTimeListAdapter.getCount();
            long[] arr = new long[count];
            for (int i = 0; i<count; i++) {
                arr[i] = mTimeListAdapter.getItem(i);
            }
            outState.putLongArray("times", arr);
        }

        TextView counter = (TextView) findViewById(R.id.counter);
        if (counter != null)
            outState.putCharSequence("currentTime", counter.getText());
        super.onSaveInstanceState(outState);
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
}
