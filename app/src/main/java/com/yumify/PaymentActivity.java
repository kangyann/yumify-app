package com.yumify;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PaymentActivity  extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Intent intent = getIntent();

        String invoice = intent.getStringExtra("invoiceNumber");

        Log.d("PaymentInvoiceNumber ", invoice);
        getTransaction(invoice);
    }
    public void getTransaction(String invoice) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url("https://yumify-api.vercel.app/api/transaction?invoices=" + invoice).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("PaymentInvoiceNumber ", "ERROR FETCHING TRANSACTION." + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String body = response.body().string();
                try {
                    JSONObject responseJson = new JSONObject(body);

                    Log.d("PaymentInvoiceNumber ", responseJson.toString());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
