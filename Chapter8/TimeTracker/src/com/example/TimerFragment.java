package com.example;

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

public class TimerFragment extends Fragment {
    
    private TextView mCounter;
    private Button mStartButton;
    private Button mEditButton;
    private TextView mName;
    private TextView mDescription;
    private TextView mDate;
    private Button mStartStop;
    private GestureDetector mGestureDetector;
    
    public void setName(String name) {
        mName.setText(name);
    }
    
    public void setDate(String date) {
        mDate.setText(date);
    }

    public void setDescription(String description) {
        mDescription.setText(description);
    }
    
    public void setCounter(String count) {
        mCounter.setText(count);
    }
    
    private TextView setNameAndText(View v, int nameId, String value) {
        TextView name = (TextView) v.findViewById(R.id.name);
        TextView text = (TextView) v.findViewById(R.id.text);
        String s = getResources().getString(nameId);
        name.setText(s);
        text.setText(value);
        return text;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.task_detail, null);
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

        mStartButton = (Button) activity.findViewById(R.id.start_stop);
        mStartButton.setOnClickListener(activity);

        mEditButton = (Button) activity.findViewById(R.id.edit);
        mEditButton.setOnClickListener(activity);
        
        mStartStop = (Button) activity.findViewById(R.id.start_stop);
        
        View v = activity.findViewById(R.id.task_name);
        mName = setNameAndText(v, R.string.detail_name, getResources().getString(R.string.task_name));
        
        v = activity.findViewById(R.id.task_date);
        String date = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(), TimeTrackerActivity.DATE_FLAGS);
        mDate = setNameAndText(v, R.string.detail_date, date);
        
        v = activity.findViewById(R.id.task_desc);
        mDescription = setNameAndText(v, R.string.detail_desc, getResources().getString(R.string.description));
        
        if (savedInstanceState != null)
            setupTextViews(savedInstanceState);
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putCharSequence("name", mName.getText());
        outState.putCharSequence("date", mDate.getText());
        outState.putCharSequence("description", mDescription.getText());
        outState.putCharSequence("time", mCounter.getText());
        outState.putCharSequence("startstop", mStartButton.getText());

        super.onSaveInstanceState(outState);
    }

    private void setupTextViews(Bundle bundle) {
        String s = bundle.getString("time");
        if (s != null)
            mCounter.setText(s);

        s = bundle.getString("name");
        if (s != null)
            mName.setText(s);

        s = bundle.getString("date");
        if (s != null)
            mDate.setText(s);

        s = bundle.getString("description");
        if (s != null)
            mDescription.setText(s);
        
        s = bundle.getString("startstop");
        if (s != null)
            mStartButton.setText(s);
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
