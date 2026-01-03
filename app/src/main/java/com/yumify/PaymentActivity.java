package com.yumify;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.yumify.lib.FormatCurrency;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

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

        getTransaction(invoice);

        Button buttonRefresh = findViewById(R.id.buttonRefreshPayment);
        buttonRefresh.setOnClickListener(v -> {
            getTransaction(invoice);
        });
    }
    public void getTransaction(String invoice) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url("https://yumify-api.vercel.app/api/transaction?invoices=" + invoice)
                .build();

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
                    JSONObject data = responseJson.getJSONObject("data");

                    String paymentString = data.getString("paymentString");
                    String orderId = data.getString("invoiceNumber");
                    String status = data.getString("status");

                    int totalPayment = data.getInt("totalPrice");

                        runOnUiThread( () -> {

                            ImageView paymentImage = findViewById(R.id.paymentImage);
                            TextView paymentTotalPrice = findViewById(R.id.paymentTotalPrice);
                            ProgressBar progressPaymentImage = findViewById(R.id.progressPaymentImage);
                            TextView paymentOrderId = findViewById(R.id.paymentOrderId);

                            ConstraintLayout paymentSuccessComponent = findViewById(R.id.paymentSuccessComponent);
                            ConstraintLayout paymentErrorComponent = findViewById(R.id.paymentErrorComponent);
                            FrameLayout paymentFrame = findViewById(R.id.paymentFrame);

                            String totalText = new FormatCurrency().Get(totalPayment);

                            Glide.with(PaymentActivity.this)
                                    .load(paymentString)
                                    .into(paymentImage);

                            if(status.equals("SETTLEMENT")){
                                paymentSuccessComponent.setVisibility(View.VISIBLE);
                                paymentErrorComponent.setVisibility(View.GONE);
                                paymentFrame.setVisibility(View.GONE);
                                thenRedirect();
                            } else if (status.equals("PENDING")) {
                                paymentFrame.setVisibility(View.VISIBLE);
                                paymentErrorComponent.setVisibility(View.GONE);
                                paymentSuccessComponent.setVisibility(View.GONE);
                            } else {
                                paymentErrorComponent.setVisibility(View.VISIBLE);
                                paymentSuccessComponent.setVisibility(View.GONE);
                                paymentFrame.setVisibility(View.GONE);
                                thenRedirect();
                            }

                            paymentTotalPrice.setText(getString(R.string.payment_box_total) + totalText);
                            paymentOrderId.setText(getString(R.string.payment_box_order_id) + orderId);

                            progressPaymentImage.setVisibility(TextView.GONE);
                            paymentImage.setVisibility(TextView.VISIBLE);
                    });

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    protected void thenRedirect(){
        new Handler().postDelayed(() -> {
                Intent intent = new Intent(PaymentActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
        },1500);
    }
}
