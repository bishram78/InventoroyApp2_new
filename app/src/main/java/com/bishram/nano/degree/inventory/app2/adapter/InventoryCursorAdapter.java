package com.bishram.nano.degree.inventory.app2.adapter;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.bishram.nano.degree.inventory.app2.R;

import static com.bishram.nano.degree.inventory.app2.data.InventoryContract.InventoryEntry;

public class InventoryCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link InventoryCursorAdapter}
     *
     * @param context The context.
     * @param cursor The cursor from which to get the data.
     */
    public InventoryCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0 /* Flags. */);
    }

    /**
     * Makes a new view to hold the data pointed to by cursor.
     *
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in the list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * Bind an existing view to the data pointed to by cursor
     *
     * @param view    Existing view, returned earlier by newView
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTexView = view.findViewById(R.id.product_name_tv);
        TextView priceTextView = view.findViewById(R.id.product_price_tv);
        final TextView quantityTextView = view.findViewById(R.id.product_quantity_tv);
        final Button buttonSellProduct = view.findViewById(R.id.product_sell_btn);

        // Find the columns of inventory attributes that we're interested in
        final int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_NAME_PRODUCT);
        final int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE_PRODUCT);
        final int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY_PRODUCT);
        final int soldColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SOLD_PRODUCT);
        final int supplierColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_NAME_SUPPLIER);

        // Read the inventory attributes from the Cursor for the current inventory.
        final String productName = cursor.getString(nameColumnIndex);
        final String supplierName = cursor.getString(supplierColumnIndex);
        String productPrice = "INR " + cursor.getString(priceColumnIndex);
        String productQuantity = cursor.getString(quantityColumnIndex);
        String productSold = cursor.getString(soldColumnIndex);
        String productQuantityStr = productQuantity + " Item(s) in stock";

        // Update the TextView with the attributes for the current inventory
        nameTexView.setText(productName);
        priceTextView.setText(productPrice);
        quantityTextView.setText(productQuantityStr);

        final long id = cursor.getLong(cursor.getColumnIndex(InventoryEntry._ID));
        final Uri currentUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
        final int quantity = Integer.parseInt(productQuantity);
        final int sold = Integer.parseInt(productSold);
        buttonSellProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentResolver resolver = v.getContext().getContentResolver();
                ContentValues values = new ContentValues();
                int curQuantity = quantity;
                int curSold = sold;
                if (quantity > 0) {
                    values.put(InventoryEntry.COLUMN_NAME_PRODUCT, productName);
                    values.put(InventoryEntry.COLUMN_NAME_SUPPLIER, supplierName);
                    values.put(InventoryEntry.COLUMN_QUANTITY_PRODUCT, --curQuantity);
                    values.put(InventoryEntry.COLUMN_SOLD_PRODUCT, ++curSold);
                    resolver.update(
                            currentUri,
                            values,
                            null,
                            null
                    );
                    context.getContentResolver().notifyChange(currentUri, null);
                } else {
                    buttonSellProduct.setVisibility(View.INVISIBLE);
                    quantityTextView.setText("Out of stock.");
                }
            }
        });
    }
}
