package com.bishram.nano.degree.inventory.app2.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bishram.nano.degree.inventory.app2.R;

import static com.bishram.nano.degree.inventory.app2.data.InventoryContract.InventoryEntry;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_INVENTORY_LOADER = 0;
    private Uri mCurrentInventoryUri;

    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSoldEditText;
    private EditText mSupplierEditText;
    private EditText mEmailEditText;
    private EditText mMobileEditText;
    private ImageView mSaveButton;
    private ImageView mDeleteButton;
    private ImageView mDialButton;
    private ImageView mSellIncrease;
    private ImageView mSellDecrease;
    private TextView mDeleteTextView;
    private TextView mDialTextView;

    private boolean mInventoryHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        /**
         * Called when a touch event is dispatched to a view. This allows listeners to
         * get a chance to respond before the target view.
         *
         * @param v     The view the touch event has been dispatched to.
         * @param event The MotionEvent object containing full information about
         *              the event.
         * @return True if the listener has consumed the event, false otherwise.
         */
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mInventoryHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentInventoryUri = intent.getData();

        // find all relevant views
        mNameEditText = findViewById(R.id.product_name_et);
        mPriceEditText = findViewById(R.id.product_price_et);
        mQuantityEditText = findViewById(R.id.product_quantity_et);
        mSoldEditText = findViewById(R.id.product_sold_et);
        mSupplierEditText = findViewById(R.id.supplier_name_et);
        mEmailEditText = findViewById(R.id.supplier_email_et);
        mMobileEditText = findViewById(R.id.supplier_mobile_et);
        mSaveButton = findViewById(R.id.save_inventory_iv);
        mDeleteButton = findViewById(R.id.delete_inventory_iv);
        mDialButton = findViewById(R.id.dial_supplier_iv);
        mSellIncrease = findViewById(R.id.product_sold_inc_iv);
        mSellDecrease = findViewById(R.id.product_sold_dec_iv);
        mDeleteTextView = findViewById(R.id.delete_tv);
        mDialTextView = findViewById(R.id.dial_tv);

        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSoldEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mEmailEditText.setOnTouchListener(mTouchListener);
        mMobileEditText.setOnTouchListener(mTouchListener);

        if (mCurrentInventoryUri == null) {
            setTitle(R.string.add_new_product);
            mDeleteButton.setVisibility(View.INVISIBLE);
            mDeleteTextView.setVisibility(View.INVISIBLE);
            mDialTextView.setVisibility(View.INVISIBLE);
            mDialButton.setVisibility(View.INVISIBLE);
        } else {
            setTitle(R.string.edit_product_info);
            getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }

        mSellDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subtractOne();
                mInventoryHasChanged = true;
            }
        });

        mSellIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                additionOne();
                mInventoryHasChanged =  true;
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProduct();
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProduct();
            }
        });

        mDialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialSupplier();
            }
        });
    }

    private void saveProduct() {
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String soldString = mSoldEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        String emailString = mEmailEditText.getText().toString().trim();
        String mobileString = mMobileEditText.getText().toString().trim();

        if (mCurrentInventoryUri == null &&
                TextUtils.isEmpty(nameString) &&
                TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) &&
                TextUtils.isEmpty(soldString) &&
                TextUtils.isEmpty(supplierString) &&
                TextUtils.isEmpty(emailString) &&
                TextUtils.isEmpty(mobileString)) {
            return;
        } else if (TextUtils.isEmpty(nameString) ||
                TextUtils.isEmpty(priceString) ||
                TextUtils.isEmpty(quantityString) ||
                TextUtils.isEmpty(soldString) ||
                TextUtils.isEmpty(supplierString) ||
                TextUtils.isEmpty(emailString) ||
                TextUtils.isEmpty(mobileString)) {
            Toast.makeText(this, "One or more fields are empty!", Toast.LENGTH_SHORT).show();
        } else {
            ContentValues values = new ContentValues();

            values.put(InventoryEntry.COLUMN_NAME_PRODUCT, nameString);
            values.put(InventoryEntry.COLUMN_PRICE_PRODUCT, priceString);
            values.put(InventoryEntry.COLUMN_QUANTITY_PRODUCT, quantityString);
            values.put(InventoryEntry.COLUMN_SOLD_PRODUCT, soldString);
            values.put(InventoryEntry.COLUMN_NAME_SUPPLIER, supplierString);
            values.put(InventoryEntry.COLUMN_EMAIL_SUPPLIER, emailString);
            values.put(InventoryEntry.COLUMN_MOBILE_SUPPLIER, mobileString);

            if (mCurrentInventoryUri == null) {
                Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

                if (newUri == null) {
                    Toast.makeText(this, "Error saving product", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Product saved successfully", Toast.LENGTH_SHORT).show();
                }
            } else {
                int rowsAffected = getContentResolver().update(mCurrentInventoryUri, values, null, null);

                if (rowsAffected == 0) {
                    Toast.makeText(this, "Error updating product", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        }
    }

    private void deleteProduct() {
        showDeleteConfirmationDialog();
    }

    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteCurrentProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteCurrentProduct() {
        if (mCurrentInventoryUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentInventoryUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.error_deletion),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.successful_deletion),
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    private void dialSupplier() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:03456780"));

        if (ActivityCompat.checkSelfPermission(EditorActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);
    }

    private void additionOne() {
        String previousValueString = mSoldEditText.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            previousValue = 0;
        } else {
            previousValue = Integer.parseInt(previousValueString);
        }
        mSoldEditText.setText(String.valueOf(previousValue + 1));
    }

    private void subtractOne() {
         String previousValueString = mSoldEditText.getText().toString();
         int previousValue;
         if (previousValueString.isEmpty()) {
             return;
         } else if (previousValueString.equals("0")) {
             return;
         } else {
             previousValue = Integer.parseInt(previousValueString);
             mSoldEditText.setText(String.valueOf(previousValue - 1));
         }
     }

    @Override
    public void onBackPressed() {
        //Go back if we have no changes
        if (!mInventoryHasChanged) {
            super.onBackPressed();
            return;
        }

        //otherwise Protect user from loosing info
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the item.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_NAME_PRODUCT,
                InventoryEntry.COLUMN_PRICE_PRODUCT,
                InventoryEntry.COLUMN_QUANTITY_PRODUCT,
                InventoryEntry.COLUMN_SOLD_PRODUCT,
                InventoryEntry.COLUMN_NAME_SUPPLIER,
                InventoryEntry.COLUMN_EMAIL_SUPPLIER,
                InventoryEntry.COLUMN_MOBILE_SUPPLIER
        };

        return new CursorLoader(this,
                mCurrentInventoryUri,
                projection,
                null,
                null,
                null
        );
    }

    /**
     * Called when a previously created loader has finished its load.  Note
     * that normally an application is <em>not</em> allowed to commit fragment
     * transactions while in this call, since it can happen after an
     * activity's state is saved.  See {@link FragmentManager#beginTransaction()
     * FragmentManager.openTransaction()} for further discussion on this.
     *
     * <p>This function is guaranteed to be called prior to the release of
     * the last data that was supplied for this Loader.  At this point
     * you should remove all use of the old data (since it will be released
     * soon), but should not do your own release of the data since its Loader
     * owns it and will take care of that.  The Loader will take care of
     * management of its data so you don't have to.  In particular:
     *
     * <ul>
     * <li> <p>The Loader will monitor for changes to the data, and report
     * them to you through new calls here.  You should not monitor the
     * data yourself.  For example, if the data is a {@link Cursor}
     * and you place it in a {@link CursorAdapter}, use
     * the  constructor <em>without</em> passing
     * in either {@link CursorAdapter#FLAG_AUTO_REQUERY}
     * or {@link CursorAdapter#FLAG_REGISTER_CONTENT_OBSERVER}
     * (that is, use 0 for the flags argument).  This prevents the CursorAdapter
     * from doing its own observing of the Cursor, which is not needed since
     * when a change happens you will get a new Cursor throw another call
     * here.
     * <li> The Loader will release the data once it knows the application
     * is no longer using it.  For example, if the data is
     * a {@link Cursor} from a {@link CursorLoader},
     * you should not call close() on it yourself.  If the Cursor is being placed in a
     * {@link CursorAdapter}, you should use the
     * {@link CursorAdapter#swapCursor(Cursor)}
     * method so that the old Cursor is not closed.
     * </ul>
     *
     * @param loader The Loader that has finished.
     * @param cursor   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int titleColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_NAME_PRODUCT);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE_PRODUCT);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY_PRODUCT);
            int soldColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SOLD_PRODUCT);
            int supplierColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_NAME_SUPPLIER);
            int emailColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_EMAIL_SUPPLIER);
            int mobileColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_MOBILE_SUPPLIER);

            String stringName = cursor.getString(titleColumnIndex);
            String stringPrice = cursor.getString(priceColumnIndex);
            String stringQuantity = cursor.getString(quantityColumnIndex);
            String stringSold = cursor.getString(soldColumnIndex);
            String stringSupplier = cursor.getString(supplierColumnIndex);
            String stringEmail = cursor.getString(emailColumnIndex);
            String stringMobile = cursor.getString(mobileColumnIndex);

            mNameEditText.setText(stringName);
            mPriceEditText.setText(stringPrice);
            mQuantityEditText.setText(stringQuantity);
            mSoldEditText.setText(stringSold);
            mSupplierEditText.setText(stringSupplier);
            mEmailEditText.setText(stringEmail);
            mMobileEditText.setText(stringMobile);
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSoldEditText.setText("");
        mSupplierEditText.setText("");
        mEmailEditText.setText("");
        mMobileEditText.setText("");
    }
}
