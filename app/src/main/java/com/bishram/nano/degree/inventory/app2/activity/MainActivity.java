package com.bishram.nano.degree.inventory.app2.activity;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.bishram.nano.degree.inventory.app2.R;
import com.bishram.nano.degree.inventory.app2.adapter.InventoryCursorAdapter;

import java.util.Random;

import static com.bishram.nano.degree.inventory.app2.data.InventoryContract.InventoryEntry;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the inventory data loader.
     */
    private static final int INVENTORY_LOADER = 0;

    /**
     * Adapter for the ListView.
     *
     */
    InventoryCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.main_activity_title));

        // Find a ListView which will be populated with the inventory data
        ListView listView = findViewById(R.id.list);

        /*
         * Find and set empty view on the listView, so that it only shows when the
         * list has 0 items to display.
         */
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        /*
         * Setup an Adapter to Create a list item for each row of inventory data
         * in the Cursor. There is no inventory data yet (Until the loader finishes)
         * so pass in null for the Cursor.
         */
        mCursorAdapter = new InventoryCursorAdapter(this, null);
        listView.setAdapter(mCursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Random random = new Random();
                double productPriceDouble = random.nextInt(50000 - 1000)/10.0;
                String productPriceStr = String.format("%.2f", productPriceDouble);
                Toast.makeText(getApplicationContext(), productPriceStr, Toast.LENGTH_SHORT).show();
            }
        });

        // Kick off the loader.
        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
    }

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.
     *
     * <p>This is only called once, the first time the options menu is
     * displayed.  To update the menu every time it is displayed, see
     * {@link #onPrepareOptionsMenu}.
     *
     * <p>The default implementation populates the menu with standard system
     * menu items.  These are placed in the {@link Menu#CATEGORY_SYSTEM} group so that
     * they will be correctly ordered with application-defined menu items.
     * Deriving classes should always call through to the base implementation.
     *
     * <p>You can safely hold on to <var>menu</var> (and any items created
     * from it), making modifications to it as desired, until the next
     * time onCreateOptionsMenu() is called.
     *
     * <p>When you add items to the menu, you can implement the Activity's
     * {@link #onOptionsItemSelected} method to handle them there.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     *
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.</p>
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_main_insert_dummy_product:
                // Respond to a click on the "Insert Dummy Product" menu option.
                insetInventory();
                return true;

            case R.id.action_main_delete_all_data:
                // Respond to a click on the "Delete All Data" menu option.
                deleteAllInventory();
                return true;

            case R.id.action_main_exit_app:
                // Respond to a click on the "Exit App" menu option.
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to insert hardcoded product data into the database.
     * This is only for debugging purposes only.
     */
    private void insetInventory() {

        Random random = new Random();
        String productName = "Product_" + random.nextInt(5000 - 1000);
        double productPriceDouble = random.nextInt(50000 - 1000)/10.0;
        String productPriceStr = String.format("%.2f", productPriceDouble);
        int productQuantity = random.nextInt(1000 - 10);
        int productSold = random.nextInt(100 - 1);
        String supplierName = "Supplier_" + random.nextInt(100 - 1);
        String supplierEmail = "abc." + random.nextInt(999 - 100) + "@email.com";
        long mobileNumber = random.nextInt(70000 - 10000) * 13;

        /*
         * Create a ContentValues object where column names are the keys,
         * and "Item_"'s product attributes are the values.
         */
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_NAME_PRODUCT, productName);
        values.put(InventoryEntry.COLUMN_PRICE_PRODUCT, productPriceStr);
        values.put(InventoryEntry.COLUMN_QUANTITY_PRODUCT, productQuantity);
        values.put(InventoryEntry.COLUMN_SOLD_PRODUCT, productSold);
        values.put(InventoryEntry.COLUMN_NAME_SUPPLIER, supplierName);
        values.put(InventoryEntry.COLUMN_EMAIL_SUPPLIER, supplierEmail);
        values.put(InventoryEntry.COLUMN_MOBILE_SUPPLIER, mobileNumber);

        /*
         * Insert a new row for "Item_" product into the provider using the ContentResolver.
         * Use the {@link #CONTENT_URI} to indicate that we want to insert into the
         * inventory database table.
         *
         * Receive the new content URI that will allow us to access "Item_"'s data
         * in the future.
         */
        Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

        if (newUri == null) {
            Toast.makeText(this, getString(R.string.error_insertion), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getString(R.string.successful_insertion), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Helper method to delete all inventory in the database.
     */
    private void deleteAllInventory() {
        int rowsDeleted = getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);

        if (rowsDeleted != 0) {
            Toast.makeText(this, getString(R.string.error_deletion), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getString(R.string.successful_deletion), Toast.LENGTH_LONG).show();
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns the table we care about
        String[] projection = {
          InventoryEntry._ID,
          InventoryEntry.COLUMN_NAME_PRODUCT,
          InventoryEntry.COLUMN_PRICE_PRODUCT,
          InventoryEntry.COLUMN_QUANTITY_PRODUCT,
          InventoryEntry.COLUMN_SOLD_PRODUCT
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                InventoryEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Update {@link InventoryCursorAdapter} with this new cursor
        // containing updated inventory data.
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted.
        mCursorAdapter.swapCursor(null);
    }
}
