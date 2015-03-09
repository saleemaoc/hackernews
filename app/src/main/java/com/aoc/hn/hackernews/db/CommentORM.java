package com.aoc.hn.hackernews.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.aoc.hn.hackernews.models.CommentItem;

import java.util.ArrayList;
import java.util.List;

public class CommentORM {

    public static final String TAG = "CommentORM";
    public static final String TABLE_NAME = "comments";
    private static final String COMMA_SEP = ", ";

    private static final String COLUMN_ID_TYPE = "INTEGER PRIMARY KEY";
    private static final String COLUMN_ID = "LocalId";

    private static final String COLUMN_SERVER_ID_TYPE = "INTEGER";
    private static final String COLUMN_SERVER_ID = "id";

    private static final String COLUMN_AUTHOR_TYPE = "TEXT";
    private static final String COLUMN_AUTHOR = "author";

    private static final String COLUMN_TIME_TYPE = "INTEGER";
    private static final String COLUMN_TIME = "time";

    private static final String COLUMN_CONTENT_TYPE = "TEXT";
    private static final String COLUMN_CONTENT = "content";

    private static final String COLUMN_REPLY_TYPE = "TEXT";
    private static final String COLUMN_REPLY = "latest_reply";

    private static final String COLUMN_REPLY_ID_TYPE = "TEXT";
    private static final String COLUMN_REPLY_ID = "latest_reply_id";

    public static final String SQL_CREATE_TABLE =
        "CREATE TABLE " + TABLE_NAME + " (" +
        COLUMN_ID + " " + COLUMN_ID_TYPE + COMMA_SEP +
        COLUMN_SERVER_ID + " " + COLUMN_SERVER_ID_TYPE + COMMA_SEP +
        COLUMN_AUTHOR + " " + COLUMN_AUTHOR_TYPE + COMMA_SEP +
        COLUMN_TIME + " " + COLUMN_TIME_TYPE + COMMA_SEP +
        COLUMN_CONTENT + " " + COLUMN_CONTENT_TYPE + COMMA_SEP +
        COLUMN_REPLY_ID + " " + COLUMN_REPLY_ID_TYPE + COMMA_SEP +
        COLUMN_REPLY + " " + COLUMN_REPLY_TYPE +
        ")";

    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    /**
     * Fetches the full list of objects stored in the local Database
     * @param context
     * @return
     */
    public static List<CommentItem> getAll(Context context, ArrayList<String> filters) {
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(context);
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        List<CommentItem> list = null;
        if(database != null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + CommentORM.TABLE_NAME, null);
            Log.i(TAG, "Loaded " + cursor.getCount() + " objects...");
            if(cursor.getCount() > 0) {
                list = new ArrayList<CommentItem>();
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    CommentItem si = cursorToObject(cursor);
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
    public static CommentItem findById(Context context, long objectId) {
        if(objectId == -1 || context == null) {
            return null;
        }
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(context);
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        CommentItem ci = null;
        if(database != null) {
            Log.i(TAG, "Loading object["+objectId+"]...");
            Cursor cursor = database.rawQuery("SELECT * FROM " + CommentORM.TABLE_NAME + " WHERE " + CommentORM.COLUMN_SERVER_ID + " = " + objectId, null);

            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                ci = cursorToObject(cursor);
                Log.i(TAG, "Object loaded successfully!");
            }
            database.close();
        }
        return ci;
    }
    
    public static boolean insert(Context context, CommentItem ci) {
        if(context == null) {
            return false;
        }
        if(ci.id >= 0 && findById(context, ci.id) != null) {
            Log.i(TAG, "Object already exists in database, not inserting!");
            return true;
        }
        ContentValues values = objToContentValues(ci);
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(context);
        SQLiteDatabase database = databaseWrapper.getWritableDatabase();
        boolean success = false;
        try {
            if (database != null) {
                long objectId = database.insert(CommentORM.TABLE_NAME, "null", values);
                Log.i(TAG, "Inserted new Object with ID: " + objectId);
                success = true;
            }
        } catch (NullPointerException ex) {
            Log.e(TAG, "Failed to insert object[" + ci.id + "] due to: " + ex);
        } finally {
            if(database != null) {
                database.close();
            }
        }
        return success;
    }

    private static ContentValues objToContentValues(CommentItem ci) {
        ContentValues values = new ContentValues();
        values.put(CommentORM.COLUMN_SERVER_ID, ci.id);
        values.put(CommentORM.COLUMN_AUTHOR, ci.author);
        values.put(CommentORM.COLUMN_TIME, ci.time);
        values.put(CommentORM.COLUMN_CONTENT, ci.content);
        values.put(CommentORM.COLUMN_REPLY, ci.latestReply);
        values.put(CommentORM.COLUMN_REPLY_ID, ci.latestReplyId);
        return values;
    }

    private static CommentItem cursorToObject(Cursor cursor) {
        CommentItem ci = new CommentItem();
        ci.id = cursor.getLong(cursor.getColumnIndex(COLUMN_SERVER_ID));
        ci.author = cursor.getString(cursor.getColumnIndex(COLUMN_AUTHOR));
        ci.time = cursor.getLong(cursor.getColumnIndex(COLUMN_TIME));
        ci.content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT));
        ci.latestReply = cursor.getString(cursor.getColumnIndex(COLUMN_REPLY));
        ci.latestReplyId = cursor.getString(cursor.getColumnIndex(COLUMN_REPLY_ID));
        return ci;
    }

    public static boolean clearAll(Context context) {
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(context);
        SQLiteDatabase database = databaseWrapper.getWritableDatabase();
        boolean success = false;
        try {
            if (database != null) {
            	database.execSQL(SQL_DROP_TABLE);
            	database.execSQL(CommentORM.SQL_CREATE_TABLE);
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