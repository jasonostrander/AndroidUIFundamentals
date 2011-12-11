package com.example;

import com.example.provider.TaskProvider;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EditTaskActivity extends FragmentActivity implements OnClickListener, LoaderCallbacks<Cursor> {

    public static final String TASK_ID = "TaskId";
    private long mTaskId;
    private EditText mName;
    private EditText mDescription;
    private long mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_task);
        
        if (savedInstanceState != null) {
            mTaskId = savedInstanceState.getLong(TASK_ID);
        }
        
        Button finish = (Button) findViewById(R.id.finish);
        finish.setOnClickListener(this);
        
        mName = (EditText) findViewById(R.id.name);
        mDescription = (EditText) findViewById(R.id.description);
        
        getSupportLoaderManager().initLoader(0, null, this);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
        // Save newly entered data to the database
        Uri uri = TaskProvider.CONTENT_URI_WITH_TASK;
        String selection = TaskProvider.Task._ID + " = ?";
        String[] selectionArgs = new String[] {Long.toString(mTaskId)};
        ContentValues cv = new ContentValues();
        cv.put(TaskProvider.Task.NAME, mName.getText().toString());
        cv.put(TaskProvider.Task.DESCRIPTION, mDescription.getText().toString());
        mQueryHandler.startUpdate(0, null, uri, cv, selection, selectionArgs);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.finish) {
            finish();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        Uri uri = TaskProvider.CONTENT_URI_WITH_TASK;
        String[] projection = new String[] {
                TaskProvider.Task.NAME,
                TaskProvider.Task.DATE,
                TaskProvider.Task.DESCRIPTION
                };
        String selection = TaskProvider.Task._ID + " = ?";
        String[] selectionArgs = new String[] {Long.toString(mTaskId)};
        return new CursorLoader(this, uri, projection, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int nameIndex = cursor.getColumnIndexOrThrow(TaskProvider.Task.NAME);
        int descIndex = cursor.getColumnIndexOrThrow(TaskProvider.Task.DESCRIPTION);
        mName.setText(cursor.getString(nameIndex));
        mDescription.setText(cursor.getString(descIndex));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
    }
    
    private AsyncQueryHandler mQueryHandler = new AsyncQueryHandler(getContentResolver()) {
    };
}
