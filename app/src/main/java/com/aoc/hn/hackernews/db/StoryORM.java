package com.aoc.hn.hackernews.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.aoc.hn.hackernews.obj.StoryItem;

public class StoryORM {

    public static final String TAG = "StoryORM";
    public static final String TABLE_NAME = "stories";
    private static final String COMMA_SEP = ", ";

    private static final String COLUMN_ID_TYPE = "INTEGER PRIMARY KEY";
    private static final String COLUMN_ID = "LocalId";

    private static final String COLUMN_SERVER_ID_TYPE = "INTEGER";
    private static final String COLUMN_SERVER_ID = "id";

    private static final String COLUMN_AUTHOR_TYPE = "TEXT";
    private static final String COLUMN_AUTHOR = "author";

    private static final String COLUMN_TITLE_TYPE = "TEXT";
    private static final String COLUMN_TITLE = "title";

    private static final String COLUMN_SCORE_TYPE = "INTEGER";
    private static final String COLUMN_SCORE = "score";

    private static final String COLUMN_TIME_TYPE = "INTEGER";
    private static final String COLUMN_TIME = "time";

    private static final String COLUMN_URL_TYPE = "TEXT";
    private static final String COLUMN_URL = "url";

    private static final String COLUMN_KIDS_TYPE = "TEXT";
    private static final String COLUMN_KIDS = "kids";

    public static final String SQL_CREATE_TABLE =
        "CREATE TABLE " + TABLE_NAME + " (" +
        COLUMN_ID + " " + COLUMN_ID_TYPE + COMMA_SEP +
        COLUMN_SERVER_ID + " " + COLUMN_SERVER_ID_TYPE + COMMA_SEP +

        COLUMN_AUTHOR + " " + COLUMN_AUTHOR_TYPE + COMMA_SEP +
        COLUMN_TITLE + " " + COLUMN_TITLE_TYPE + COMMA_SEP +
        COLUMN_SCORE + " " + COLUMN_SCORE_TYPE + COMMA_SEP +
        COLUMN_TIME + " " + COLUMN_TIME_TYPE + COMMA_SEP +
        COLUMN_KIDS + " " + COLUMN_KIDS_TYPE + COMMA_SEP +
        COLUMN_URL + " " + COLUMN_URL_TYPE +
        ")";

    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    /**
     * Fetches the full list of objects stored in the local Database
     * @param context
     * @return
     */
    public static List<StoryItem> getAll(Context context, ArrayList<String> filters) {
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(context);
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        List<StoryItem> list = null;
        if(database != null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + StoryORM.TABLE_NAME, null);
            Log.i(TAG, "Loaded " + cursor.getCount() + " objects...");
            if(cursor.getCount() > 0) {
                list = new ArrayList<StoryItem>();
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    StoryItem si = cursorToObject(cursor);
                	if(si.id != -1) {
                		list.add(si);
                	}
                    cursor.moveToNext();
                }
                Log.i(TAG, "objects loaded successfully.");
            }
            database.close();
        }
        return list;
    }

    /**
     * Fetches a single Object identified by the specified ID
     * @param context
     * @param objectId
     * @return
     */
    public static StoryItem findById(Context context, long objectId) {
        if(objectId == -1) {
            return null;
        }
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(context);
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        StoryItem si = null;
        if(database != null) {
            Log.i(TAG, "Loading object["+objectId+"]...");
            Cursor cursor = database.rawQuery("SELECT * FROM " + StoryORM.TABLE_NAME + " WHERE " + StoryORM.COLUMN_SERVER_ID + " = " + objectId, null);

            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                si = cursorToObject(cursor);
                Log.i(TAG, "Object loaded successfully!");
            }

            database.close();
        }

        return si;
    }
    
    public static boolean insert(Context context, StoryItem si) {
        if(si.id >= 0 && findById(context, si.id) != null) {
            Log.i(TAG, "Object already exists in database, not inserting!");
            return true;//update(context, l2o);
        }
        ContentValues values = objToContentValues(si);
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(context);
        SQLiteDatabase database = databaseWrapper.getWritableDatabase();
        boolean success = false;
        try {
            if (database != null) {
                long objectId = database.insert(StoryORM.TABLE_NAME, "null", values);
                Log.i(TAG, "Inserted new Object with ID: " + objectId);
                success = true;
            }
        } catch (NullPointerException ex) {
            Log.e(TAG, "Failed to insert object[" + si.id + "] due to: " + ex);
        } finally {
            if(database != null) {
                database.close();
            }
        }
        return success;
    }

    private static ContentValues objToContentValues(StoryItem si) {
        ContentValues values = new ContentValues();
        values.put(StoryORM.COLUMN_SERVER_ID, si.id);
        values.put(StoryORM.COLUMN_AUTHOR, si.author);
        values.put(StoryORM.COLUMN_TITLE, si.title);
        values.put(StoryORM.COLUMN_SCORE, si.points);
        values.put(StoryORM.COLUMN_TIME, si.time);
        values.put(StoryORM.COLUMN_URL, si.url);
        String kids = "";
        if(si.comments != null) {
            for (String k : si.comments) {
                kids += k + ":";
            }
            kids = kids.substring(0, kids.length()-1);
        }
        values.put(StoryORM.COLUMN_KIDS, kids);
        return values;
    }

    private static StoryItem cursorToObject(Cursor cursor) {
        StoryItem si = new StoryItem();
        si.id = cursor.getLong(cursor.getColumnIndex(COLUMN_SERVER_ID));
        si.author = cursor.getString(cursor.getColumnIndex(COLUMN_AUTHOR));
        si.title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
        si.points = cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE));
        si.time = cursor.getLong(cursor.getColumnIndex(COLUMN_TIME));
        si.url = cursor.getString(cursor.getColumnIndex(COLUMN_URL));
        String kids = cursor.getString(cursor.getColumnIndex(COLUMN_KIDS));
        si.comments = Arrays.asList(kids.split(":"));
        return si;
    }

    public static boolean clearAll(Context context) {
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(context);
        SQLiteDatabase database = databaseWrapper.getWritableDatabase();
        boolean success = false;
        try {
            if (database != null) {
            	database.execSQL(SQL_DROP_TABLE);
            	database.execSQL(StoryORM.SQL_CREATE_TABLE);
            }
        } catch (NullPointerException ex) {
        } finally {
            if(database != null) {
                database.close();
            }
        }
        return success;
    }
    
}