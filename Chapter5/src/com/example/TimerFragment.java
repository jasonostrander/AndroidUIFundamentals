package com.example;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class TimerFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_detail, container);
        return view;
    }

    
    private void setDescAndText(Activity activity, int id, int desc, String value) {
        View v = activity.findViewById(id);
        TextView name = (TextView) v.findViewById(R.id.name);
        TextView text = (TextView) v.findViewById(R.id.text);
        String s = getResources().getString(desc);
        name.setText(s);
        text.setText(value);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TimeTrackerActivity activity = (TimeTrackerActivity) getActivity();
        
        // Initialize the Timer
        TextView counter = (TextView) activity.findViewById(R.id.counter);
        counter.setText(DateUtils.formatElapsedTime(0));

        Button startButton = (Button) activity.findViewById(R.id.start_stop);
        startButton.setOnClickListener(activity);

        Button editButton = (Button) activity.findViewById(R.id.edit);
        editButton.setOnClickListener(activity);

        long date = System.currentTimeMillis();
        if (savedInstanceState != null) {
            CharSequence seq = savedInstanceState.getCharSequence("currentTime");
            if (seq != null)
                counter.setText(seq);
            
            date = savedInstanceState.getLong("dateTime", System.currentTimeMillis());
        }

        String text = getResources().getString(R.string.task_name);
        setDescAndText(activity, R.id.task_name, R.string.detail_name, text);
        text = DateUtils.formatDateTime(activity, date, TimeTrackerActivity.DATE_FLAGS);
        setDescAndText(activity, R.id.task_date, R.string.detail_date, text);
        text = getResources().getString(R.string.lorem_ipsum);
        setDescAndText(activity, R.id.task_desc, R.string.detail_desc, text);
    }
}
