package com.example;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.provider.TaskProvider;

public class TaskListFragment extends ListFragment implements LoaderCallbacks<Cursor> {

    TimeListAdapter mAdapter;
    TaskListener mListener;
    
    public static interface TaskListener {
        public void onTaskSelected(long id, String name, String desc, long date, long time);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // indicate this fragment adds a menu option
        setHasOptionsMenu(true);
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
        long date = cursor.getLong(cursor.getColumnIndexOrThrow(TaskProvider.Task.DATE));
        int time = cursor.getInt(cursor.getColumnIndexOrThrow(TaskProvider.Task.TIME));
        mListener.onTaskSelected(id, name, desc, date, time);
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.clear_all:
            //Testing
            FragmentManager fm = getActivity().getSupportFragmentManager();
            if (fm.findFragmentByTag("dialog") == null) {
                ConfirmClearDialogFragment frag = ConfirmClearDialogFragment.newInstance();
                frag.show(fm, "dialog");
            }
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
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
