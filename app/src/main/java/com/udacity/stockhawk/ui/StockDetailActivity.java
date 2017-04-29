package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import timber.log.Timber;

public class StockDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Extra to carry symbol
     */
    public static final String EXTRA_SYMBOL = "extra_symbol";

    /**
     * Stock detail loader id
     */
    public static final int STOCK_DETAIL_LOADER = 1;

    /**
     * Symbol of stock shown in this activity
     */
    private String symbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        symbol = getIntent().getStringExtra(EXTRA_SYMBOL);
        Timber.d("symbol retrieved - " + symbol);
        getSupportLoaderManager().initLoader(STOCK_DETAIL_LOADER, null, this);
    }

    @Override
    public CursorLoader onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                Contract.Quote.makeUriForStock(symbol),
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                null, null, Contract.Quote.COLUMN_SYMBOL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        String history = data.getString(Contract.Quote.POSITION_HISTORY);
        Timber.e("History - " + history); // TODO: YahooFinance API is broken, need to finish this after fix is found
        data.close();
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
