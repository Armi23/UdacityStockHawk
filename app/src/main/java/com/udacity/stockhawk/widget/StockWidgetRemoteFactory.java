package com.udacity.stockhawk.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.ui.StockDetailActivity;

/**
 * Factory used for the stock widget
 */

public class StockWidgetRemoteFactory implements RemoteViewsService.RemoteViewsFactory {

    /**
     * Cursor used to track data of stock
     */
    private Cursor cursor;

    /**
     * Context used to access content provider
     */
    private Context context;

    /**
     * Constuctor
     *
     * @param context context used to access content provider
     */
    public StockWidgetRemoteFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        updateData();
    }

    @Override
    public void onDestroy() {
        if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    public int getCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        cursor.moveToPosition(i);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.list_item_quote);
        remoteViews.setTextViewText(R.id.symbol, cursor.getString(Contract.Quote.POSITION_SYMBOL));
        remoteViews.setTextViewText(R.id.price, cursor.getString(Contract.Quote.POSITION_PRICE));
        remoteViews.setTextViewText(R.id.change, cursor.getString(Contract.Quote.POSITION_ABSOLUTE_CHANGE));

        Intent intent = new Intent();
        intent.putExtra(StockDetailActivity.EXTRA_SYMBOL, cursor.getString(Contract.Quote.POSITION_SYMBOL));
        remoteViews.setOnClickFillInIntent(R.id.widget_item, intent);
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * Updates data used for factory
     */
    private void updateData() {
        if (cursor != null) {
            cursor.close();
        }
        cursor = context.getContentResolver().query(Contract.Quote.URI, null, null, null, null);
    }
}
