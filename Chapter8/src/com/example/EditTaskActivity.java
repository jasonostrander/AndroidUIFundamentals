package com.example;

import java.util.Calendar;

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
    public static final String TASK_TIME = "TaskTime";
    public static final String TASK_NAME = "TaskName";
    public static final String TASK_DATE = "TaskDATE";
    public static final String TASK_DESCRIPTION = "TaskDescription";
    
    private long mTaskId = -1;
    private long mTime = 0;
    private EditText mName;
    private EditText mDescription;
    private DatePicker mDate;

    private long getDateMillis() {
        int year = mDate.getYear();
        int month = mDate.getMonth();
        int dayOfMonth = mDate.getDayOfMonth();
        Calendar c = Calendar.getInstance();
        c.set(year, month, dayOfMonth);
        return c.getTimeInMillis();
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_task);
        
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mTaskId = extras.getLong(TASK_ID);
            mTime = extras.getLong(TASK_TIME);
        }
        
        Button finish = (Button) findViewById(R.id.finished);
        finish.setOnClickListener(this);
        
        mName = (EditText) findViewById(R.id.name);
        mDescription = (EditText) findViewById(R.id.description);
        mDate = (DatePicker) findViewById(R.id.date);
        
        getSupportLoaderManager().initLoader(0, null, this);
    }
    
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.finished) {
            // Save newly entered data to the database
            // Don't block the UI thread
            AsyncQueryHandler handler = new AsyncQueryHandler(getContentResolver()) {};

            Uri uri = TaskProvider.getContentUri();
            ContentValues cv = new ContentValues();
            cv.put(TaskProvider.Task.NAME, mName.getText().toString());
            cv.put(TaskProvider.Task.DESCRIPTION, mDescription.getText().toString());
            cv.put(TaskProvider.Task.DATE, getDateMillis());
            
            if (mTaskId > -1) {
                String selection = TaskProvider.Task._ID + " = ?";
                String[] selectionArgs = new String[] {Long.toString(mTaskId)};
                handler.startUpdate(0, null, uri, cv, selection, selectionArgs);
            } else {
                cv.put(TaskProvider.Task.TIME, 0);
                handler.startInsert(0, null, uri, cv);
            }

            // Now finish the task, returning a result to the TimeTrackerActivity
            Intent data = new Intent();
            data.putExtra(TASK_ID, mTaskId);
            data.putExtra(TASK_TIME, mTime);
            data.putExtra(TASK_NAME, mName.getText().toString());
            data.putExtra(TASK_DATE, getDateMillis());
            data.putExtra(TASK_DESCRIPTION, mDescription.getText().toString());
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        Uri uri = TaskProvider.getContentUri();
        String[] projection = new String[] {
                TaskProvider.Task.NAME,
                TaskProvider.Task.DESCRIPTION,
                TaskProvider.Task.DATE,
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
        long date = cursor.getLong(cursor.getColumnIndexOrThrow(TaskProvider.Task.DATE));
        
        mName.setText(name);
        mDescription.setText(desc);
        
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date);
        mDate.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
