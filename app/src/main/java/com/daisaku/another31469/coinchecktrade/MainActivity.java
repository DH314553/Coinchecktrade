package com.daisaku.another31469.coinchecktrade;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.daisaku.another31469.coinchecktrade.api.CoinCheckApi;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CoinCheckApi coinCheckApi;

    private LineChart mChart;

    //Apiで取得されたjsonを使う
    JSONObject object;

    //btc最新価格
    String last;

    //btc値
    String amount;

    List<PyObject> array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mChart = findViewById(R.id.line_chart);
        EditText apiKey = findViewById(R.id.apiKey);
        EditText secretKey = findViewById(R.id.secretKey);
        Button register = findViewById(R.id.register);
        register.setOnClickListener(view -> {
            coinCheckApi = new CoinCheckApi(apiKey.getText().toString(),
                    secretKey.getText().toString());
        });
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        Python py = Python.getInstance();
        PyObject module = py.getModule("data");
        array = module.asList();
        try {
            object = new JSONObject();
            JSONObject tickerArray = object.getJSONObject(coinCheckApi.getTicker());
            JSONObject tradesArray = object.getJSONObject(coinCheckApi.getTrades());
            last = tickerArray.getString("last");
            amount = tradesArray.getString("amount");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Grid背景色
        mChart.setDrawGridBackground(true);

        // no description text
        mChart.getDescription().setEnabled(true);

        // Grid縦軸を破線
        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = mChart.getAxisLeft();
        // Y軸最大最小設定
        leftAxis.setAxisMaximum(150f);
        leftAxis.setAxisMinimum(0f);
        // Grid横軸を破線
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(true);

        // 右側の目盛り
        mChart.getAxisRight().setEnabled(false);

        // add data
        setData();

        mChart.animateX(2500);
        mChart.invalidate();
    }

    private void setData() {
        // Entry()を使ってLineDataSetに設定できる形に変更してarrayを新しく作成
        int[] data = {
                Integer.parseInt(String.valueOf(array))
        };

        ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < data.length; i++) {
            values.add(new Entry(i, data[i], null, null));
        }

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {

            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "DataSet");

            set1.setDrawIcons(false);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(0f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            set1.setFillColor(Color.BLUE);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData lineData = new LineData(dataSets);

            // set data
            mChart.setData(lineData);
        }
    }
}