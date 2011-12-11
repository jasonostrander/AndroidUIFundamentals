package com.example;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.provider.TaskProvider;

public class TimerFragment extends Fragment implements LoaderCallbacks<Cursor> {

    long mTaskId = -1;
    
    public void setTaskId(long id) {
        mTaskId = id;
        getLoaderManager().restartLoader(0, null, this);
    }
    
    public long getTaskId() {
        return mTaskId;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.task_detail, null);
    }

    
    private void setDescAndText(int id, int desc, String value) {
        View v = getActivity().findViewById(id);
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

        setupTextViews(null);
        
        getLoaderManager().initLoader(0, null, this);
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
        setDescAndText(R.id.task_name, R.string.detail_name, name); 
        setDescAndText(R.id.task_date, R.string.detail_date, date);
        setDescAndText(R.id.task_desc, R.string.detail_desc, desc);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = TaskProvider.CONTENT_URI_WITH_TASK;
        String[] projection = new String[] {
                TaskProvider.Task.NAME,
                TaskProvider.Task.DATE,
                TaskProvider.Task.DESCRIPTION
                };
        String selection = TaskProvider.Task._ID + " = ?";
        String[] selectionArgs = new String[] {Long.toString(mTaskId)};
        return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        setupTextViews(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        setupTextViews(null);
    }
}
