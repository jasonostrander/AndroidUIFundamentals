package com.example;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
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


    private TextView mCounter;
    private View mName;
    private View mDescription;
    private View mDate;
    private Button mStartStop;
    private GestureDetector mGestureDetector;
    
    public void setName(String name) {
        setNameAndText(mName, R.string.detail_name, name); 
    }
    
    public void setDate(long date) {
        setNameAndText(mDate, R.string.detail_date, Long.toString(date));
    }
    
    public void setDescription(String description) {
        setNameAndText(mDescription, R.string.detail_desc, description);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.task_detail, null);
    }
    
    private void setNameAndText(View v, int nameId, String value) {
        TextView name = (TextView) v.findViewById(R.id.name);
        TextView text = (TextView) v.findViewById(R.id.text);
        String s = getResources().getString(nameId);
        name.setText(s);
        text.setText(value);
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
        
        mStartStop = (Button) activity.findViewById(R.id.start_stop);

        long date = System.currentTimeMillis();
        if (savedInstanceState != null) {
            CharSequence seq = savedInstanceState.getCharSequence("currentTime");
            if (seq != null)
                mCounter.setText(seq);
            
            date = savedInstanceState.getLong("dateTime", System.currentTimeMillis());
        }

        mName = activity.findViewById(R.id.task_name);
        mDate = activity.findViewById(R.id.task_date);
        mDescription = activity.findViewById(R.id.task_desc);
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
        setNameAndText(mName, R.string.detail_name, name); 
        setNameAndText(mDate, R.string.detail_date, date);
        setNameAndText(mDescription, R.string.detail_desc, desc);
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
