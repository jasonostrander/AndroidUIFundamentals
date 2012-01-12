package com.example;

import android.app.Activity;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.provider.TaskProvider;

public class TimerFragment extends Fragment {

    private LinearLayout mCounter;
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
        mCounter = (LinearLayout) activity.findViewById(R.id.counter);
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
            CharSequence time = savedInstanceState.getCharSequence("currentTime");
//            if (time != null)
//                setCounterTime(time);
            
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
    
    public void setCounterTime(long time) {
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
