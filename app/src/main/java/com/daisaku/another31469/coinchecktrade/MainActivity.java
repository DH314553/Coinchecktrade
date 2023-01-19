package com.daisaku.another31469.coinchecktrade;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.daisaku.another31469.coinchecktrade.api.CoinCheckApi;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    CoinCheckApi coinCheckApi = new CoinCheckApi("Ruy-zSSNbbvh9OUe",
            "VQGlYgm_R5WYQGi_adSMybMVtFBILR7_");

    JSONObject object;

    //btc最新価格
    String last;

    //btc値
    String amount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            object = new JSONObject();
            JSONObject tickerArray = object.getJSONObject(coinCheckApi.getTicker());
            JSONObject tradesArray = object.getJSONObject(coinCheckApi.getTrades());
            last = tickerArray.getString("last");
            amount = tradesArray.getString("amount");
            coinCheckApi.orderBuy(last, "0.005");
            coinCheckApi.orderSell(last, amount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}