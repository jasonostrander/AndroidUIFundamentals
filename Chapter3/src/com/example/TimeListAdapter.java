package com.example;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TimeListAdapter extends ArrayAdapter<Long> {
    
    public TimeListAdapter(Context context, int textViewResourceId, List<Long> list) {
        super(context, textViewResourceId, list);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.time_row, null);
        }
        
        long time = getItem(position);
        
        TextView name = (TextView) view.findViewById(R.id.lap_name);
        String taskString = getContext().getResources().getString(R.string.task_name);
        name.setText(String.format(taskString, position+1));
        
        TextView lapTime = (TextView) view.findViewById(R.id.lap_time);
        lapTime.setText(DateUtils.formatElapsedTime(time));
        
        return view;
    }
}
