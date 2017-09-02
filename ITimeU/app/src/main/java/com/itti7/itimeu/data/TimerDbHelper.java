package com.itti7.itimeu.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Admin on 2017-08-16.
 */

public class TimerDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ITimeU.db";

    public TimerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TimerContract.TimerEntry.TABLE_NAME + " (" +
                        TimerContract.TimerEntry.COLUMN_REPEAT + " INTEGER)";
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
/*         This database is only a cache for online data, so its upgrade policy is
         to simply to discard the data and start over
        final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TimerContract.TimerEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);*/
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
