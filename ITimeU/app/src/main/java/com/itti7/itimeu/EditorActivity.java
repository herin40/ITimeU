package com.itti7.itimeu;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.itti7.itimeu.data.ItemContract.ItemEntry;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Allows user to create a new item or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the item data loader */
    private static final int EXISTING_ITEM_LOADER = 0;

    /** Content URI for the existing item (null if it's a new item) */
    private Uri mCurrentItemUri;

    /** EditText field to enter the item's name */
    private EditText mNameEditText;

    /** EditText field to enter the item's detail */
    private EditText mDetailEditText;

    /** EditText field to enter the item's date */
    private EditText mDateEditText;

    /** TextView field to enter the item's total unit */
    private TextView mTotalUnitTextView;

    /** ImageButton to increase total unit number */
    private ImageButton mUnitPlusImageButton;

    /** ImageButton to decrease total unit number */
    private ImageButton mUnitMinusImageButton;

    /** Total unit convert integer value */
    private int mTotalUnitNumber;

    /** Total unit convert string value */
    private String mTotalUnitString;

    /** Creation date */
    private String mDate;

    // Simple date format
    public static final String DATE_FORMAT = "yyyy.MM.dd";

    // Date year, month, day;
    private int mYear, mMonth, mDay;

    /**
     * Status of the item. The possible valid values are in the ItemContract.java file:
     * {@link ItemEntry#STATUS_TODO}, {@link ItemEntry#STATUS_DO}, {@link ItemEntry#STATUS_DONE}
     * */
    private int mStatus = ItemEntry.STATUS_TODO;

    /** Boolean flag that keeps track of whether the item has been edited (true) or not (false) */
    private boolean mItemHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mItemHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new item or editing an existing one.
        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        // If the intent DOES NOT contain a item content URI, then we know that we are
        // creating a new item.
        TextView titleTextView = (TextView) findViewById(R.id.editor_title_txt_view);
        if (mCurrentItemUri == null) {
            // This is a new item, so change the title in TextView "ADD"
            titleTextView.setText(R.string.editor_title_add);
        } else {
            // Otherwise this is an existing item, so change the title in TextView "EDIT"
            titleTextView.setText(R.string.editor_title_edit);

            // Initialize a loader to read the item data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.name_edit_txt);
        mDetailEditText = (EditText) findViewById(R.id.detail_edit_txt);
        mTotalUnitTextView = (TextView) findViewById(R.id.get_total_unit_txt_view);
        mDate = intent.getStringExtra("date");
        mDateEditText = (EditText) findViewById(R.id.editor_date_edit_txt);
        mTotalUnitNumber = Integer.parseInt(mTotalUnitTextView.getText().toString().trim());

        mUnitMinusImageButton = (ImageButton) findViewById(R.id.unit_minus_btn);
        mUnitPlusImageButton = (ImageButton) findViewById(R.id.unit_plus_btn);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mDetailEditText.setOnTouchListener(mTouchListener);
        mUnitMinusImageButton.setOnTouchListener(mTouchListener);
        mUnitPlusImageButton.setOnTouchListener(mTouchListener);

        // Get the date selected by the user.
        dateSelection();

        // change total unit number
        getTotalUnitNumber();

        // click ok
        submit();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int selectedYear, int selectedMonth, int selectedDay) {
        Calendar calendar = Calendar.getInstance();

        // Assign Selected Date in DatePickerDialog
        mYear = selectedYear;
        mMonth = selectedMonth;
        mDay = selectedDay;

        // Set Date in List
        calendar.set(mYear, mMonth, mDay);
        Date date = calendar.getTime();
        mDate = getDate(date);
        mDateEditText.setText(mDate);
    }

    void dateSelection(){
        mDateEditText.setFocusable(false);
        mDateEditText.setOnTouchListener(mTouchListener);
        mDateEditText.setText(mDate);
        mDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.KOREA);
                try {
                    // String -> Date
                    Date date = format.parse(mDate);

                    // Setting calender -> list's date
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    mYear = calendar.get(Calendar.YEAR);
                    mMonth = calendar.get(Calendar.MONTH);
                    mDay = calendar.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog
                            = DatePickerDialog.newInstance(EditorActivity.this, mYear, mMonth, mDay);
                    datePickerDialog.show(getFragmentManager(), "DateFragment");
                }
                catch (ParseException e) {
                    Log.e("EditorActivity", "ParseException: " + e);
                }
            }
        });
    }

    /**
     * Get user input from editor and save item into database.
     */
    private boolean saveItem() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String detailString = mDetailEditText.getText().toString().trim();

        // Check whether name edit text is empty.
        // if name is empty return false, else save the item than return true
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        if (TextUtils.isEmpty(nameString)) {
            mNameEditText.startAnimation(shake);
            Toast.makeText(this, getString(R.string.input_name_toast), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Log.v("EditorActivity", mDate);
            // Determine if this is a new or existing item by checking
            // if mCurrentItemUri is null or not
            if (mCurrentItemUri == null) {
                // Create  a ContentValues object for a new Item
                ContentValues createValues = new ContentValues();
                createValues.put(ItemEntry.COLUMN_ITEM_NAME, nameString);
                createValues.put(ItemEntry.COLUMN_ITEM_DETAIL, detailString);
                createValues.put(ItemEntry.COLUMN_ITEM_TOTAL_UNIT, mTotalUnitNumber);
                createValues.put(ItemEntry.COLUMN_ITEM_STATUS, mStatus);
                createValues.put(ItemEntry.COLUMN_ITEM_DATE, mDate);
                Log.v("EditorActivity", mDate);

                // This is a NEW item, so insert a new item into the provider,
                // returning the content URI for the new item.
                Uri newUri = getContentResolver().insert(ItemEntry.CONTENT_URI, createValues);

                // Show a toast message depending on whether or not the insertion was successful.
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(this, getString(R.string.create_item_fail), Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.create_item_success), Toast.LENGTH_SHORT).show();
                }
            } else {
                // Create a ContentValues object for a existing item
                ContentValues editValues = new ContentValues();
                editValues.put(ItemEntry.COLUMN_ITEM_NAME, nameString);
                editValues.put(ItemEntry.COLUMN_ITEM_DETAIL, detailString);
                editValues.put(ItemEntry.COLUMN_ITEM_TOTAL_UNIT, mTotalUnitNumber);
                editValues.put(ItemEntry.COLUMN_ITEM_DATE, mDate);

                // Otherwise this is an EXISTING item, so update the item with content URI: mCurrentItemUri
                // and pass in the new ContentValues. Pass in null for the selection and selection args
                // because mCurrentItemUri will already identify the correct row in the database that
                // we want to modify.
                int rowsAffected = getContentResolver().update(mCurrentItemUri, editValues, null, null);

                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update.
                    Toast.makeText(this, getString(R.string.update_item_fail), Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the update was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.update_item_success), Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        }
    }

    /**
     * Save or cancel items.
     */
    private void submit() {
        Button okButton = (Button) findViewById(R.id.add_ok_btn);
        Button cancelButton = (Button) findViewById(R.id.add_cancel_btn);

        //click ok button
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // success to insert item
                if (saveItem()) finish();
            }
        });

        //click cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemHasChanged) {
                    // if there are unsaved changes, setup a dialog to warn the user.
                    // Create a click listener to handle the user confirming that
                    // changes should be discarded.
                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // User clicked "Discard" button, navigate to parent activity.
                                    finish();
                                }
                            };

                    // Show a dialog that notifies the user they have unsaved changes
                    showUnsavedChangesDialog(discardButtonClickListener);
                }
                else finish();
            }
        });
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Since the editor shows all item attributes, define a projection that contains
        // all columns from the item table
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_ITEM_NAME,
                ItemEntry.COLUMN_ITEM_DETAIL,
                ItemEntry.COLUMN_ITEM_DATE,
                ItemEntry.COLUMN_ITEM_TOTAL_UNIT,
                ItemEntry.COLUMN_ITEM_UNIT,
                ItemEntry.COLUMN_ITEM_STATUS
        };

        String[] date = {mDate};
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentItemUri,         // Query the content URI for the current item
                projection,             // Columns to include in the resulting Cursor
                "date = ?",                   // No selection clause
                date,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_NAME);
            int detailColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_DETAIL);
            //int unitColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_UNIT);
            int totalUnitColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_TOTAL_UNIT);
            //int statusColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_STATUS);
            int dateColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_DATE);

            String name = cursor.getString(nameColumnIndex);
            String detail = cursor.getString(detailColumnIndex);
            String date = cursor.getString(dateColumnIndex);
            //int unit = cursor.getInt(unitColumnIndex);
            int totalUnit = cursor.getInt(totalUnitColumnIndex);
            //int status = cursor.getInt(statusColumnIndex);

            mTotalUnitString = Integer.toString(totalUnit);

            mNameEditText.setText(name);
            mDetailEditText.setText(detail);
            mTotalUnitNumber = totalUnit;
            mTotalUnitTextView.setText(mTotalUnitString);
            mDate = date;
            mDateEditText.setText(mDate);

            getTotalUnitNumber();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mDetailEditText.setText("");
        mTotalUnitTextView.setText(getString(R.string.reset_total_unit));
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
        builder.setMessage(getString(R.string.unsaved_change_msg));
        builder.setPositiveButton(getString(R.string.discard_btn), discardButtonClickListener);
        builder.setNegativeButton(getString(R.string.keep_editing_btn), new DialogInterface.OnClickListener() {
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
     * this function change unit number in unit text view according to plus/minus image button.
     * minimum number: 1 / maximum number: 20
     * ut
     */
    private void getTotalUnitNumber() {
        getUnitImageButtonSrc();
        // increase unit number
        mUnitPlusImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTotalUnitNumber < 20) {
                    mUnitPlusImageButton.setImageResource(R.drawable.ic_unit_plus_true);
                    mTotalUnitNumber++;
                    mTotalUnitString = Integer.toString(mTotalUnitNumber);
                    mTotalUnitTextView.setText(mTotalUnitString);
                }
                getUnitImageButtonSrc();
            }
        });

        // decrease unit number
        mUnitMinusImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTotalUnitNumber > 1) {
                    mTotalUnitNumber--;
                    mTotalUnitString = Integer.toString(mTotalUnitNumber);
                    mTotalUnitTextView.setText(mTotalUnitString);
                }
                getUnitImageButtonSrc();
            }
        });
    }


    /**
     * This function change plus/minus imageButton src according to Unit number range
     */
    private void getUnitImageButtonSrc() {
        if (mTotalUnitNumber <= 1) {
            mUnitMinusImageButton.setImageResource(R.drawable.ic_unit_minus_false);
            mUnitPlusImageButton.setImageResource(R.drawable.ic_unit_plus_true);
        } else if (mTotalUnitNumber < 20) {
            mUnitMinusImageButton.setImageResource(R.drawable.ic_unit_minus_true);
            mUnitPlusImageButton.setImageResource(R.drawable.ic_unit_plus_true);
        } else {
            mUnitMinusImageButton.setImageResource(R.drawable.ic_unit_minus_true);
            mUnitPlusImageButton.setImageResource(R.drawable.ic_unit_plus_false);
        }
    }

    /**
     * It is a function of today's date.
     *
     * @return Return the current month and day.
     */
    public String getDate(Date date) {
        return new SimpleDateFormat(DATE_FORMAT, Locale.KOREA).format(date);
    }

    /**
     * @param view          DatePickerDialog
     * @param selectedYear  Year selected by the user
     * @param selectedMonth Month selected by the user
     * @param selectedDay   Date selected by the user
     */
}