package com.itti7.itimeu;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.CursorAdapter;
import android.widget.TextView;

import com.itti7.itimeu.data.ItemContract;

/**
 * {@link ItemCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of item data as its data source. This adapter knows
 * how to create list items for each row of item data in the {@link Cursor}.
 */

class ItemCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link ItemCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item, parent, false);
    }

    /**
     * This method binds the item data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current item can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = view.findViewById(R.id.name_txt_view);
        TextView detailTextView = view.findViewById(R.id.detail_txt_view);
        TextView totalUnitTextView = view.findViewById(R.id.total_unit_txt_view);
        TextView unitTextView = view.findViewById(R.id.unit_txt_view);

        // Find the columns of item attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_NAME);
        int quantityColumnIdex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_DETAIL);
        int totalUnitColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_TOTAL_UNIT);
        int unitColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_UNIT);

        // Read the item attributes from the Cursor for the current item
        String itemName = cursor.getString(nameColumnIndex);
        String itemQuantity = cursor.getString(quantityColumnIdex);
        int itemTotalUnit = cursor.getInt(totalUnitColumnIndex);
        int itemUnit = cursor.getInt(unitColumnIndex);

        String itemTotalUnitString = Integer.toString(itemTotalUnit);
        String itemUnitString = Integer.toString(itemUnit);

        // Update the TextViews with the attributes for the current item
        nameTextView.setText(itemName);
        detailTextView.setText(itemQuantity);
        totalUnitTextView.setText(itemTotalUnitString);
        unitTextView.setText(itemUnitString);
    }
}
