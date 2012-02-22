package com.example.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Content provider for timer tasks. 
 * 
 */
public class TaskProvider extends ContentProvider {
    private static final String TABLE_NAME = "TimerTasks";
    
    private static final String AUTHORITY = "com.example.TimeTracker";
    private static final String SCHEME = "content://";
    public static final Uri getContentUri() {
        return Uri.parse(SCHEME + AUTHORITY + "/task");
    }
    public static final Uri getContentUriWithTask(long id) {
        return ContentUris.withAppendedId(getContentUri(), id);
    }

    public static final class Task implements BaseColumns {
        public static final String NAME = "Name";
        public static final String TIME = "Time";
        public static final String DATE = "Date";
        public static final String DESCRIPTION = "Description";
        public static final String ACTIVE = "Active";
    }
    
    private static final int VERSION = 1;
    
    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, TABLE_NAME, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                    + Task._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Task.NAME + " TEXT,"
                    + Task.TIME + " INTEGER,"
                    + Task.DESCRIPTION + " TEXT,"
                    + Task.DATE + " INTEGER,"
                    + Task.ACTIVE + " BOOLEAN"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Version 1 so does nothing
        }
    }

    private DatabaseHelper mDatabaseHelper;

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public int delete(Uri arg0, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int rows = db.delete(TABLE_NAME, selection, selectionArgs);
        
        // Notify cursors of change
        Uri result = getContentUri();
        getContext().getContentResolver().notifyChange(result, null);
        return rows;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        long id = db.insert(TABLE_NAME, null, values);
        
        if (id > 0) {
            Uri result = getContentUriWithTask(id);
            getContext().getContentResolver().notifyChange(result, null);
            return result;
        }
        throw new SQLiteException("Failed to insert row using: " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        Cursor c = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int result = db.update(TABLE_NAME, values, selection, selectionArgs);
        if (result > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return result;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}
