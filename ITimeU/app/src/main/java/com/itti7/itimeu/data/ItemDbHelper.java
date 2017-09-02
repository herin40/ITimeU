package com.itti7.itimeu.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.itti7.itimeu.data.ItemContract.ItemEntry;

/**
 * Database helper for I Time U app. Manages database creation and version management.
 */
public class ItemDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ItemDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "iTimeU.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link ItemDbHelper}.
     *
     * @param context of the app
     */

    public ItemDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the items table
        String SQL_CREATE_LIST_TABLE = "CREATE TABLE " + ItemEntry.TABLE_NAME + "("
                + ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ItemEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_ITEM_DETAIL + " TEXT, "
                + ItemEntry.COLUMN_ITEM_DATE + " TEXT, "
                + ItemEntry.COLUMN_ITEM_TOTAL_UNIT + " INTEGER NOT NULL DEFAULT 1, "
                + ItemEntry.COLUMN_ITEM_UNIT + " INTEGER NOT NULL DEFAULT 0, "
                + ItemEntry.COLUMN_ITEM_STATUS + " INTEGER NOT NULL DEFAULT 0);";

        db.execSQL(SQL_CREATE_LIST_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
