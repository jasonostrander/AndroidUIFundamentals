package com.example;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TimeListAdapter extends ArrayAdapter<Time> {
    
    private List<Time> times = new ArrayList<Time>();
    
    public TimeListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }
    
    @Override
    public void add(Time object) {
        times.add(object);
        super.add(object);
    }
    
    @Override
    public int getCount() {
        return times.size();
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.time_row, null);
        }
        
        Time time = times.get(position);
        
        TextView name = (TextView) view.findViewById(R.id.lap_name);
        name.setText(time.name);
        
        TextView lapTime = (TextView) view.findViewById(R.id.lap_time);
        lapTime.setText(time.time);
        
        return view;
    }
}
