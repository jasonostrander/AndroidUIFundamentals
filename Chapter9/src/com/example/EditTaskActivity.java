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
    private long mTaskId = -1;
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
        
        Button finish = (Button) findViewById(R.id.finished);
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
            @Override
            protected void onInsertComplete(int token, Object cookie, Uri uri) {
                super.onInsertComplete(token, cookie, uri);
                long task = Long.parseLong(uri.getLastPathSegment());
                Intent data = new Intent();
                data.putExtra(TASK_ID, task);
                setResult(RESULT_OK, data);
            }
            
            @Override
            protected void onUpdateComplete(int token, Object cookie, int result) {
                super.onUpdateComplete(token, cookie, result);
            }
        };

        Uri uri = TaskProvider.getContentUri();
        ContentValues cv = new ContentValues();
        cv.put(TaskProvider.Task.NAME, mName.getText().toString());
        cv.put(TaskProvider.Task.DESCRIPTION, mDescription.getText().toString());
        
        if (mTaskId > -1) {
            String selection = TaskProvider.Task._ID + " = ?";
            String[] selectionArgs = new String[] {Long.toString(mTaskId)};
            handler.startUpdate(0, null, uri, cv, selection, selectionArgs);
        } else {
            cv.put(TaskProvider.Task.DATE, System.currentTimeMillis());
            cv.put(TaskProvider.Task.TIME, 0);
            handler.startInsert(0, null, uri, cv);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.finished) {
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
        // Do nothing if this task has not yet been created
        if (cursor.getCount() <= 0)
            return;
        
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
