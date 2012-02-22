package com.example;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TimerFragment extends Fragment {
    
    private LinearLayout mCounter;
    private Button mStartButton;
    private Button mEditButton;
    private TextView mName;
    private TextView mDescription;
    private TextView mDate;
    private Button mStartStop;
    private GestureDetector mGestureDetector;
    private long mCurrentTime;
    
    public void setName(String name) {
        mName.setText(name);
    }
    
    public void setDate(String date) {
        mDate.setText(date);
    }

    public void setDescription(String description) {
        mDescription.setText(description);
    }
    
    public void setCounter(long count) {
        setCounterTime(count);
        mCurrentTime = count;
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
        mCounter = (LinearLayout) activity.findViewById(R.id.counter);
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
        outState.putLong("time", mCurrentTime);
        outState.putCharSequence("startstop", mStartButton.getText());

        super.onSaveInstanceState(outState);
    }

    private void setupTextViews(Bundle bundle) {
        long time = bundle.getLong("time");
        setCounterTime(time);

        String s = bundle.getString("name");
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
    
    private void setCounterTime(long time) {
        long hours = 0;
        long minutes = 0;
        long seconds = 0;
        if (time > 3600*1000) {
            hours = time/(3600*1000);
            time -= hours*3600*1000;
        }
        if (time > 60*1000) {
            minutes = time/(60*1000);
            time -= minutes*60*1000;
        }
        if (time > 1000) {
            seconds = time/1000;
            time -= seconds*1000;
        }

        animateDigit(R.id.minute2, minutes%10);
        animateDigit(R.id.minute1, minutes/10);
        animateDigit(R.id.hour2, hours%10);
        animateDigit(R.id.hour1, hours/10);
        animateDigit(R.id.second2, seconds%10);
        animateDigit(R.id.second1, seconds/10);
    }
    
    private void animateDigit(final int id, final long value) {
        Activity activity = getActivity();
        if (activity == null) return;
        
        final View v = activity.findViewById(id);
        final TextView text1 = (TextView) v.findViewById(R.id.text1);
        final TextView text2 = (TextView) v.findViewById(R.id.text2 );

        boolean running = false;
        if (text1.getAnimation() != null)
            running = !text1.getAnimation().hasEnded();

        if (Long.parseLong(text1.getText().toString()) == value || running) return;

        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.slide_out);
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            
            @Override
            public void onAnimationEnd(Animation animation) {
                text1.setText("" + value);
            }
        });
        text1.startAnimation(animation);
        
        animation = AnimationUtils.loadAnimation(activity, R.anim.slide_in);
        text2.startAnimation(animation);
        text2.setText("" + value);
    }
}
