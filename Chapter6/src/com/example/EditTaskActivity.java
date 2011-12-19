package com.example;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Intent;
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
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.provider.TaskProvider;

public class EditTaskActivity extends FragmentActivity implements OnClickListener, LoaderCallbacks<Cursor> {

    public static final String TASK_ID = "TaskId";
    private long mTaskId;
    private EditText mName;
    private EditText mDescription;
    private DatePicker mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_task);
        
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mTaskId = extras.getLong(TASK_ID);
        }
        
        Button finish = (Button) findViewById(R.id.finish);
        finish.setOnClickListener(this);
        
        mName = (EditText) findViewById(R.id.name);
        mDescription = (EditText) findViewById(R.id.description);
        mDate = (DatePicker) findViewById(R.id.date);
        
        getSupportLoaderManager().initLoader(0, null, this);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
        // Save newly entered data to the database
        // Don't block the UI thread
        AsyncQueryHandler handler = new AsyncQueryHandler(getContentResolver()) {
        };

        Uri uri = TaskProvider.getContentUri();
        String selection = TaskProvider.Task._ID + " = ?";
        String[] selectionArgs = new String[] {Long.toString(mTaskId)};
        ContentValues cv = new ContentValues();
        cv.put(TaskProvider.Task.NAME, mName.getText().toString());
        cv.put(TaskProvider.Task.DESCRIPTION, mDescription.getText().toString());
        handler.startUpdate(0, null, uri, cv, selection, selectionArgs);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.finish) {
            finish();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        Uri uri = TaskProvider.getContentUri();
        String[] projection = new String[] {
                TaskProvider.Task.NAME,
                TaskProvider.Task.DESCRIPTION,
                TaskProvider.Task.DATE
        };
        String selection = TaskProvider.Task._ID + " = ?";
        String[] selectionArgs = new String[] {Long.toString(mTaskId)};
        return new CursorLoader(this, uri, projection, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursor.moveToFirst();
        String name = cursor.getString(cursor.getColumnIndexOrThrow(TaskProvider.Task.NAME));
        String desc = cursor.getString(cursor.getColumnIndexOrThrow(TaskProvider.Task.DESCRIPTION));
        mName.setText(name);
        mDescription.setText(desc);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
