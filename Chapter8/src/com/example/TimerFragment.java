package com.example;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.provider.TaskProvider;

public class TimerFragment extends Fragment {

    private long mDate;
    private TextView mCounter;
    private TextView mName;
    private TextView mDescription;
    private Button mStartStop;
    private GestureDetector mGestureDetector;
    
    public void setName(String name) {
        setDescAndText(R.id.task_name, R.string.detail_name, name); 
    }
    
    public String getName() {
        return (String) mName.getText();
    }
    
    public void setDate(long date) {
        mDate = date;
        setDescAndText(R.id.task_date, R.string.detail_date, Long.toString(date));
    }
    
    public long getDate() {
        return mDate;
    }
    
    public void setDescription(String description) {
        setDescAndText(R.id.task_desc, R.string.detail_desc, description);
    }
    
    public String getDescription() {
        return (String) mDescription.getText();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.task_detail, null);
    }

    
    private TextView setDescAndText(int id, int desc, String value) {
        View v = getActivity().findViewById(id);
        TextView name = (TextView) v.findViewById(R.id.name);
        TextView text = (TextView) v.findViewById(R.id.text);
        String s = getResources().getString(desc);
        name.setText(s);
        text.setText(value);
        return text;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TimeTrackerActivity activity = (TimeTrackerActivity) getActivity();
        
        mGestureDetector = new GestureDetector(activity, new DoubleTapListener());
        
        // Initialize the Timer
        mCounter = (TextView) activity.findViewById(R.id.counter);
        mCounter.setText(DateUtils.formatElapsedTime(0));
        mCounter.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                return true;
            }
        });

        Button startButton = (Button) activity.findViewById(R.id.start_stop);
        startButton.setOnClickListener(activity);

        Button editButton = (Button) activity.findViewById(R.id.edit);
        editButton.setOnClickListener(activity);

        mName = setDescAndText(R.id.task_name, R.string.detail_name, "test");
        mDescription = setDescAndText(R.id.task_name, R.string.detail_name, "testing");
        mStartStop = (Button) activity.findViewById(R.id.start_stop);
        
        long date = System.currentTimeMillis();
        if (savedInstanceState != null) {
            CharSequence seq = savedInstanceState.getCharSequence("currentTime");
            if (seq != null)
                mCounter.setText(seq);
            
            date = savedInstanceState.getLong("dateTime", System.currentTimeMillis());
        }

        setupTextViews(null);
    }

    private void setupTextViews(Cursor cursor) {
        String name = null;
        String date = null;
        String desc = null;
        if (cursor == null) {
            name = getResources().getString(R.string.task_name);
            date = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(), TimeTrackerActivity.DATE_FLAGS);
            desc = getResources().getString(R.string.lorem_ipsum); 
        } else {
            name = cursor.getString(cursor.getColumnIndexOrThrow(TaskProvider.Task.NAME));
            date = cursor.getString(cursor.getColumnIndexOrThrow(TaskProvider.Task.DATE));
            desc = cursor.getString(cursor.getColumnIndexOrThrow(TaskProvider.Task.DESCRIPTION));
        }
        setDescAndText(R.id.task_name, R.string.detail_name, name); 
        setDescAndText(R.id.task_date, R.string.detail_date, date);
        setDescAndText(R.id.task_desc, R.string.detail_desc, desc);
    }
    
    private class DoubleTapListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            TimeTrackerActivity activity = (TimeTrackerActivity) getActivity();
            activity.onClick(mStartStop);
            return true;
        }
    }
}
