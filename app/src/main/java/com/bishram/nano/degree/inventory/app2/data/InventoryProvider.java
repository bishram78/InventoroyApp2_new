package com.bishram.nano.degree.inventory.app2.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.bishram.nano.degree.inventory.app2.data.InventoryContract.CONTENT_AUTHORITY;
import static com.bishram.nano.degree.inventory.app2.data.InventoryContract.InventoryEntry;
import static com.bishram.nano.degree.inventory.app2.data.InventoryContract.PATH_INVENTORY;

/**
 * {@link ContentProvider} for Inventory app that helps insertion, deletion,
 * update table data only for right data type.
 */
public class InventoryProvider extends ContentProvider {
    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the inventory table.
     */
    private static final int INVENTORY = 100;

    /**
     * URI matcher code for the content URI for a single inventory in
     * inventory table.
     */
    private static final int INVENTORY_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the
     * root URI. It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /*
     * Static initializer.
     * This is run the first time anything is called from this class.
     */
    static {
        /*
         * The content URI of the form
         * "content://com.bishram.nano.degree.inventory.app2.data/inventory"
         * will map to the integer code {@link #INVENTORY}. This URI is used to provide
         * access to MULTIPLE rows of the inventory table.
         */
        mUriMatcher.addURI(CONTENT_AUTHORITY, PATH_INVENTORY, INVENTORY);

        /*
         * The content URI of the form
         * "content://com.bishram.nano.degree.inventory.app2/inventory/#"
         * will map to the integer code {@link #INVENTORY_ID}. This URI is used to
         * provide access to ONE single row of the inventory table.
         */
        mUriMatcher.addURI(CONTENT_AUTHORITY, PATH_INVENTORY + "/#", INVENTORY_ID);
    }

    /**
     * Database helper object.
     */
    private InventoryDatabaseHelper mDatabaseHelper;

    /**
     * Implement this to initialize your content provider on startup.
     * This method is called for all registered content providers on the
     * application main thread at application launch time.  It must not perform
     * lengthy operations, or application startup will be delayed.
     *
     * @return true if the provider was successfully loaded, false otherwise
     */
    @Override
    public boolean onCreate() {
        mDatabaseHelper = new InventoryDatabaseHelper(getContext());
        return true;
    }

    /**
     * Implement this to handle query requests from clients.
     *
     * <p>Apps targeting {@link Build.VERSION_CODES#O} or higher should override
     * {@link #query(Uri, String[], Bundle, CancellationSignal)} and provide a stub
     * implementation of this method.
     *
     * <p>This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     * <p>
     * Example client call:<p>
     * <pre>// Request a specific record.
     * Cursor managedCursor = managedQuery(
     * ContentUris.withAppendedId(Contacts.People.CONTENT_URI, 2),
     * projection,    // Which columns to return.
     * null,          // WHERE clause.
     * null,          // WHERE clause value substitution
     * People.NAME + " ASC");   // Sort order.</pre>
     * Example implementation:<p>
     * <pre>// SQLiteQueryBuilder is a helper class that creates the
     * // proper SQL syntax for us.
     * SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
     *
     * // Set the table we're querying.
     * qBuilder.setTables(DATABASE_TABLE_NAME);
     *
     * // If the query ends in a specific record number, we're
     * // being asked for a specific record, so set the
     * // WHERE clause in our query.
     * if((URI_MATCHER.match(uri)) == SPECIFIC_MESSAGE){
     * qBuilder.appendWhere("_id=" + uri.getPathLeafId());
     * }
     *
     * // Make the query.
     * Cursor c = qBuilder.query(mDb,
     * projection,
     * selection,
     * selectionArgs,
     * groupBy,
     * having,
     * sortOrder);
     * c.setNotificationUri(getContext().getContentResolver(), uri);
     * return c;</pre>
     *
     * @param uri           The URI to query. This will be the full URI sent by the client;
     *                      if the client is requesting a specific record, the URI will end in a record number
     *                      that the implementation should parse and add to a WHERE or HAVING clause, specifying
     *                      that _id value.
     * @param projection    The list of columns to put into the cursor. If
     *                      {@code null} all columns are included.
     * @param selection     A selection criteria to apply when filtering rows.
     *                      If {@code null} then all rows are included.
     * @param selectionArgs You may include ?s in selection, which will be replaced by
     *                      the values from selectionArgs, in order that they appear in the selection.
     *                      The values will be bound as Strings.
     * @param sortOrder     How the rows in the cursor should be sorted.
     *                      If {@code null} then the provider is free to define the sort order.
     * @return a Cursor or {@code null}.
     */
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // Get readable access to database.
        SQLiteDatabase database = mDatabaseHelper.getReadableDatabase();

        // Cursor that holds the result of the query.
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code.
        int match = mUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                /*
                 * For the INVENTORY code, query the pets table directly with the given
                 * projection, selection, selection arguments and sort order. The
                 * cursor could contain multiple rows of the inventory table.
                 */
                cursor = database.query(
                        InventoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case INVENTORY_ID:
                /*
                 * For the INVENTORY_ID code, extract out the ID from the URI.
                 * For every "?" in the selection, we need to have an element in
                 * the selection arguments that will fill in the "?". Since we
                 * have 1 question mark in the selection, we have 1 String in
                 * the selection arguments' String array.
                 */
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] {
                        String.valueOf(ContentUris.parseId(uri))
                };

                /*
                 * This will perform a query on the pets table where the _ID returns
                 * a Cursor containing that row of the table.
                 */
                cursor = database.query(
                        InventoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        /*
         * Set notification URI on the Cursor,
         * so we know what content URI the Cursor was created for.
         * If the data at this URI changes, then we know we need to update the Cursor.
         */
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor.
        return cursor;
    }

    /**
     * Implement this to handle requests for the MIME type of the data at the
     * given URI.  The returned MIME type should start with
     * <code>vnd.android.cursor.item</code> for a single record,
     * or <code>vnd.android.cursor.dir/</code> for multiple items.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     *
     * <p>Note that there are no permissions needed for an application to
     * access this information; if your content provider requires read and/or
     * write permissions, or is not exported, all applications can still call
     * this method regardless of their access permissions.  This allows them
     * to retrieve the MIME type for a URI when dispatching intents.
     *
     * @param uri the URI to query.
     * @return a MIME type string, or {@code null} if there is no type.
     */
    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return InventoryEntry.CONTENT_LIST_TYPE;

            case INVENTORY_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    /**
     * Implement this to handle requests to insert a new row.
     * As a courtesy, call {@link ContentResolver#notifyChange(Uri, ContentObserver) notifyChange()}
     * after inserting.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     *
     * @param uri    The content:// URI of the insertion request. This must not be {@code null}.
     * @param values A set of column_name/value pairs to add to the database.
     *               This must not be {@code null}.
     * @return The URI for the newly inserted item.
     */
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return insertInventory(uri, values);

            default:
                throw new IllegalArgumentException("Insertion is no supported for " + uri);
        }
    }

    /**
     * Insert an inventory into the database with the given content values.
     *
     * @param uri       current URI.
     * @param values    content values.
     * @return          the new content URI for that specific row in tha database.
     */
    private Uri insertInventory(Uri uri, ContentValues values) {

        // Check that the name of product is not null.
//        String nameProduct = values.getAsString(InventoryEntry.COLUMN_NAME_PRODUCT);
//        if (nameProduct == null) {
//            throw new IllegalArgumentException("Product requires a name.");
//        }
//
//        // Check that the name of supplier is not null.
//        String nameSupplier = values.getAsString(InventoryEntry.COLUMN_NAME_SUPPLIER);
//        if (nameSupplier == null) {
//            throw new IllegalArgumentException("Supplier name can not be empty.");
//        }
//
//        // Check that mobile number is valid
//        long mobileNumber = values.getAsLong(InventoryEntry.COLUMN_MOBILE_SUPPLIER);
//        if (mobileNumber != 10) {
//            throw new IllegalArgumentException("Mobile number is not valid");
//        }

        // Get writable access to the database
        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();

        // Insert the new Inventory  with the given values.

        long id = database.insert(InventoryEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the inventory URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row)
        // append at the end.
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Implement this to handle requests to delete one or more rows.
     * The implementation should apply the selection clause when performing
     * deletion, allowing the operation to affect multiple rows in a directory.
     * As a courtesy, call {@link ContentResolver#notifyChange(Uri, ContentObserver) notifyChange()}
     * after deleting.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     *
     * <p>The implementation is responsible for parsing out a row ID at the end
     * of the URI, if a specific row is being deleted. That is, the client would
     * pass in <code>content://contacts/people/22</code> and the implementation is
     * responsible for parsing the record number (22) when creating a SQL statement.
     *
     * @param uri           The full URI to query, including a row ID (if a specific record is requested).
     * @param selection     An optional restriction to apply to rows when deleting.
     * @param selectionArgs You may include ?s in selection, which will be replaced by
     *                      the values from selectionArgs, in order that they appear in the selection.
     *                      The values will be bound as Strings.
     * @return The number of rows affected.
     * @throws SQLException is an exception thrown.
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        // Get writable access to the database.
        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();

        // Track the number of rows that were deleted.
        int rowsDeleted;

        final int match = mUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                // Delete all the rows that match the selection and selection arguments.
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case INVENTORY_ID:
                // Delete a single row given by the ID in the URI
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] {
                        String.valueOf(ContentUris.parseId(uri))
                };
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        /*
         * If 1 or more rows were deleted, then notify all listeners that the
         * data at the given URI has changed.
         */
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted,
        return rowsDeleted;
    }

    /**
     * Implement this to handle requests to update one or more rows.
     * The implementation should update all rows matching the selection
     * to set the columns according to the provided values map.
     * As a courtesy, call {@link ContentResolver#notifyChange(Uri, ContentObserver) notifyChange()}
     * after updating.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     *
     * @param uri           The URI to query. This can potentially have a record ID if this
     *                      is an update request for a specific record.
     * @param values        A set of column_name/value pairs to update in the database.
     *                      This must not be {@code null}.
     * @param selection     An optional filter to match rows to update.
     * @param selectionArgs You may include ?s in selection, which will be replaced by
     *                      the values from selectionArgs, in order that they appear in the selection.
     *                      The values will be bound as Strings.
     * @return the number of rows affected.
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return updateInventory(uri, values, selection, selectionArgs);

            case INVENTORY_ID:
                /*
                 * For the INVENTORY_ID code, extract out the ID from the URI.
                 * For every "?" in the selection, we need to have an element in
                 * the selection arguments that will fill in the "?". Since we
                 * have 1 question mark in the selection, we have 1 String in
                 * the selection arguments' String array.
                 */
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] {
                        String.valueOf(ContentUris.parseId(uri))
                };
                return updateInventory(uri, values, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update inventory in the database with the given content values. Apply the
     * changes to the rows specified in the selection and selection arguments
     * (which could be 0 or 1 or more inventory).
     * @param uri           current uri.
     * @param values        current content values.
     * @param selection     A selection criteria to apply when filtering rows.
     *                      If {@code null} then all rows are included.
     * @param selectionArgs You may include ?s in selection, which will be replaced by
     *                      the values from selectionArgs, in order that they appear in the selection.
     *                      The values will be bound as Strings.
     * @return              the number of rows that were successfully updated.
     */
    private int updateInventory(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // Check that the name of product is not null.
        String nameProduct = values.getAsString(InventoryEntry.COLUMN_NAME_PRODUCT);
        if (nameProduct == null) {
            throw new IllegalArgumentException("Product requires a name.");
        }

        // Check that the name of supplier is not null.
        String nameSupplier = values.getAsString(InventoryEntry.COLUMN_NAME_SUPPLIER);
        if (nameSupplier == null) {
            throw new IllegalArgumentException("Supplier name can not be empty.");
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise get writable access to the database
        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected.
        int rowsUpdated = database.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);

        /**
         * If 1 or more rows were updated, then notify all listeners that the
         * data at the given URI changed.
         */
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated.
        return rowsUpdated;
    }
}
