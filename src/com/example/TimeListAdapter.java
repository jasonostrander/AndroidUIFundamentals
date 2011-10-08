package com.example;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TimeListAdapter extends ArrayAdapter<Time> {
    
    public Time[] times = {
            new Time("test", Utils.formatElapsedTime(0)),
            new Time("test", Utils.formatElapsedTime(0)),
            new Time("test", Utils.formatElapsedTime(0)),
            new Time("test", Utils.formatElapsedTime(0)),
            new Time("test", Utils.formatElapsedTime(0))
    };

    public TimeListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public int getCount() {
        return times.length;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.time_row, null);
        }
        
        Time time = times[position];
        
        TextView name = (TextView) view.findViewById(R.id.lap_name);
        name.setText(time.name);
        
        TextView lapTime = (TextView) view.findViewById(R.id.lap_time);
        lapTime.setText(time.time);
        
        return view;
    }
}
