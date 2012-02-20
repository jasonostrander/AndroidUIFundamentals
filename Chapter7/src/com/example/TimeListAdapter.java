package com.example;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.provider.TaskProvider;

public class TimeListAdapter extends CursorAdapter {

    public TimeListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    private static class ViewHolder {
        int nameIndex;
        int timeIndex;
        TextView name;
        TextView time;
    }
    
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.name.setText(cursor.getString(holder.nameIndex));
        long time = cursor.getLong(holder.timeIndex);
        holder.time.setText(DateUtils.formatElapsedTime(time/1000));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.time_row, null);
        ViewHolder holder = new ViewHolder();
        holder.name = (TextView) view.findViewById(R.id.task_name);
        holder.time = (TextView) view.findViewById(R.id.task_time);
        holder.nameIndex = cursor.getColumnIndexOrThrow(TaskProvider.Task.NAME);
        holder.timeIndex = cursor.getColumnIndexOrThrow(TaskProvider.Task.TIME);
        view.setTag(holder);
        return view;
    }
}
