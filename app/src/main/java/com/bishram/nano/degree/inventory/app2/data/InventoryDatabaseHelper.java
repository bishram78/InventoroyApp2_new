package com.bishram.nano.degree.inventory.app2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.bishram.nano.degree.inventory.app2.data.InventoryContract.*;

/**
 * Database helper for Inventory App which manages database creation and version management.
 */
public class InventoryDatabaseHelper extends SQLiteOpenHelper {

    /**
     * Log tag for message
     */
    public static final String LOG_TAG = InventoryDatabaseHelper.class.getSimpleName();

    /**
     * Name of the database file.
     */
    private static final String DATABASE_NAME = "inventory.db";

    /**
     * Database version of the above database file.
     * If you change the database schema, you must increment the database
     * version number.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use for locating paths to the the database.
     */
    public InventoryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        /*
         * Create a String that contains the SQL statement to create the
         * inventory table to the database file.
         */
        String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE "
                + InventoryEntry.TABLE_NAME + " ("
                + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryEntry.COLUMN_NAME_PRODUCT + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_PRICE_PRODUCT + " REAL DEFAULT 0.0, "
                + InventoryEntry.COLUMN_QUANTITY_PRODUCT + " INTEGER DEFAULT 0, "
                + InventoryEntry.COLUMN_SOLD_PRODUCT + " INTEGER DEFAULT 0, "
                + InventoryEntry.COLUMN_NAME_SUPPLIER + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_EMAIL_SUPPLIER + " TEXT, "
                + InventoryEntry.COLUMN_MOBILE_SUPPLIER + " BIGINT NOT NULL);";

        // Execute the SQL statement to create table to database file.
        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     *
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + InventoryEntry.TABLE_NAME);
        onCreate(db);
    }
}
