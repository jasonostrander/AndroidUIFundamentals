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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

public class TaskListFragment extends ListFragment implements LoaderCallbacks<Cursor> {

    TimeListAdapter mAdapter;
    TaskListener mListener;
    
    public static interface TaskListener {
        public void onTaskSelected(Uri uri);
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (TaskListener) activity;
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
        
        mAdapter = new TimeListAdapter(activity, null, 0);
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = TaskProvider.CONTENT_URI;
        String[] projection = new String[] {
                TaskProvider.Task.NAME,
                TaskProvider.Task.DATE,
                };
        return new CursorLoader(getActivity(), uri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        
    }
}
