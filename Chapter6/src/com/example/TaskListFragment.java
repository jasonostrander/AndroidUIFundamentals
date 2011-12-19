package com.example;

import com.example.provider.TaskProvider;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

public class TaskListFragment extends ListFragment implements LoaderCallbacks<Cursor> {

    TimeListAdapter mAdapter;
    TaskListener mListener;
    
    public static interface TaskListener {
        public void onTaskSelected(long id, String name, String desc, long date, int time);
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (TaskListener) activity;
        mAdapter = new TimeListAdapter(activity, null, 0);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.task_list, null);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TimeTrackerActivity activity = (TimeTrackerActivity) getActivity();
        
        Button button = (Button) activity.findViewById(R.id.new_task);
        button.setOnClickListener(activity);
        
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Cursor cursor = mAdapter.getCursor();
        cursor.moveToPosition(position);
        String name = cursor.getString(cursor.getColumnIndexOrThrow(TaskProvider.Task.NAME));
        String desc = cursor.getString(cursor.getColumnIndexOrThrow(TaskProvider.Task.DESCRIPTION));
        int time = cursor.getInt(cursor.getColumnIndexOrThrow(TaskProvider.Task.TIME));
        mListener.onTaskSelected(id, name, desc, 0, time);
    }
    
    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        Uri uri = TaskProvider.getContentUri();
        return new CursorLoader(getActivity(), uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> laoder) {
        mAdapter.swapCursor(null);
    }
}
