package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import timber.log.Timber;

public class StockActivity extends AppCompatActivity {

    /**
     * Extra to carry symbol
     */
    public static final String EXTRA_SYMBOL = "extra_symbol";

    /**
     * Symbol of stock shown in this activity
     */
    private String symbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        symbol = getIntent().getStringExtra(EXTRA_SYMBOL);
        Timber.e("symbol retrieved - " + symbol);
        Cursor cursor = getContentResolver().query(Contract.Quote.makeUriForStock(symbol), null, null, null, null);
        if (cursor == null) {
            Timber.e("Could not get anything");
            finish();
            return;
        }

        cursor.moveToFirst();
        String history = cursor.getString(Contract.Quote.POSITION_HISTORY);
        Timber.e("History - " + history); // TODO: YahooFinance API is broken, need to finish this after fix is found

        cursor.close();
    }

}
