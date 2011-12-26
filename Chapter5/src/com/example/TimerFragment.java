package com.example;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class TimerFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.task_detail, container);
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

        View v = activity.findViewById(R.id.task_name);
        String text = getResources().getString(R.string.task_name);
        setNameAndText(v, R.string.detail_name, text);
        
        v = activity.findViewById(R.id.task_date);
        text = DateUtils.formatDateTime(activity, date, TimeTrackerActivity.DATE_FLAGS);
        setNameAndText(v, R.string.detail_date, text);
        
        v = activity.findViewById(R.id.task_desc);
        text = getResources().getString(R.string.description);
        setNameAndText(v, R.string.detail_desc, text);
    }
}
