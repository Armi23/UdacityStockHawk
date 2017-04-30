package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class StockDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        OnChartValueSelectedListener {

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

    /**
     * Date formatter
     */
    private DateFormat dateFormat;

    @BindView(R.id.stock_detail_error)
    TextView error;
    @BindView(R.id.data_detail)
    TextView details;
    @BindView(R.id.chart)
    LineChart lineChart;

    /**
     * X values for graph
     */
    List<Long> xValues = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        ButterKnife.bind(this);

        dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        symbol = getIntent().getStringExtra(EXTRA_SYMBOL);
        setTitle(symbol);
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
        if (history.isEmpty()) {
            error.setVisibility(View.VISIBLE);
            lineChart.setVisibility(View.GONE);
            details.setVisibility(View.GONE);
            error.setText(R.string.error_no_history_found);
        } else {
            error.setVisibility(View.GONE);
            lineChart.setVisibility(View.VISIBLE);
            details.setVisibility(View.VISIBLE);
            xValues = new ArrayList<>();
            List<Entry> entries = new ArrayList<>();
            String[] historyData = history.split("\n");
            int i = historyData.length - 1;
            for (String point : historyData) {
                String[] values = point.split(",");
                xValues.add(Long.valueOf(values[0]));
                entries.add(new Entry(i, Float.valueOf(values[1])));
                i--;
            }
            Collections.reverse(xValues);
            Collections.reverse(entries);

            LineDataSet lineDataSet = new LineDataSet(entries, getString(R.string.graph_label));
            LineData lineData = new LineData(lineDataSet);
            lineChart.setData(lineData);

            XAxis xAxis = lineChart.getXAxis();
            xAxis.setValueFormatter(new StockDetailXAxis());
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

            lineChart.getAxisRight().setEnabled(false);

            lineChart.setOnChartValueSelectedListener(this);

            // refreshes graph
            lineChart.invalidate();
        }
        data.close();
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        int position = (int) e.getX();
        float value = e.getY();
        details.setText(getString(R.string.stock_date_info, dateFormat.format(new Date(xValues.get(position))), value));
    }

    @Override
    public void onNothingSelected() {
        details.setText(R.string.select_a_value);
    }

    /**
     * Value formatter for X axis (time) of stock detail graph
     */
    private class StockDetailXAxis implements IAxisValueFormatter {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            Long time = xValues.get((int) value);
            return dateFormat.format(new Date(time));
        }
    }

}
