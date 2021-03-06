package com.bishram.nano.degree.inventory.app2.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Inventory App.
 */
public final class InventoryContract {

    /**
     * To prevent someone from accidentally instantiating the contract class,
     * give it an empty constructor
     */
    private InventoryContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain and its website. A convenient string to use for
     * the content authority is the package name for the app, which is guaranteed
     * to be unique on the device.
     */
    public static final String CONTENT_AUTHORITY = "com.bishram.nano.degree.inventory.app2";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to
     * contact the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://package com.bishram.nano.degree.inventory.app2/inventory
     * is a valid path for looking at inventory data.
     * content://package com.bishram.nano.degree.inventory.app2/staff will fail, as
     * the ContentProvider hasn't been given any information on what to do with
     * "staff".
     */
    public static final String PATH_INVENTORY = "inventory";

    /**
     * Inner class that defines constant values for the inventory database table.
     * Each entry in the table represents a single inventory.
     */
    public static final class InventoryEntry implements BaseColumns {
        /**
         * The content URI to access the inventory data in the provider.
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of inventory.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;
        /**
         * Name of the database TABLE for the inventory.
         */
        public static final String TABLE_NAME = "inventory";

        /**
         * Unique ID number for the inventory (Only for use in the database table).
         *
         * Type: INTEGER
         */
        public static final String _ID = BaseColumns._ID;

        /**
         * Name of the product
         *
         * Type: TEXT
         * CHAR_LIMIT: NULL
         */
        public static final String COLUMN_NAME_PRODUCT = "name_product";

        /**
         * Price of the product
         *
         * Type: REAL
         */
        public static final String COLUMN_PRICE_PRODUCT = "price_product";

        /**
         * Quantity of the product
         *
         * Type: INTEGER
         */
        public static final String COLUMN_QUANTITY_PRODUCT = "quantity_product";

        /**
         * Number of product sold
         *
         * Type: INTEGER
         */
        public static final String COLUMN_SOLD_PRODUCT = "sold_product";

        /**
         * Name of the product supplier
         *
         * Type: TEXT
         */
        public static final String COLUMN_NAME_SUPPLIER = "name_supplier";

        /**
         * Email contact of the product supplier
         *
         * Type: TEXT
         */
        public static final String COLUMN_EMAIL_SUPPLIER = "email_supplier";

        /**
         * Mobile contact of the product supplier
         *
         * Type: BIGINT
         */
        public static final String COLUMN_MOBILE_SUPPLIER = "mobile_supplier";
    }
}
