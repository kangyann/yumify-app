package com.yumify;


import android.media.Image;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.yumify.lib.FormatCurrency;
import com.yumify.lib.GetUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OrderActivity extends AppCompatActivity {
    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        GetUser getUser = new GetUser(OrderActivity.this);
        JSONObject user = getUser.Load();

        String userId = user.optString("id");

        getOrder(userId);
    }

    private void getOrder(String userId) {
        Log.d("ORDERACTIVITY", userId);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .get()
                .url("https://yumify-api.vercel.app/api/transaction?invoices=none&get=all&userId=" + userId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("ORDER_ERROR", "Error: " + e.getMessage());
                runOnUiThread(() ->
                        Toast.makeText(OrderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String result = response.body().string();
                Log.d("ORDERACTIVITY", result);

                try {
                    JSONObject convert = new JSONObject(result);
                    Log.d("ORDERACTIVITY", convert.toString());
                    JSONArray getData = convert.getJSONArray("data");
                    for (int i = 0; i < getData.length(); i++) {
                        JSONObject transactions = getData.getJSONObject(i);
                        JSONArray products = transactions.getJSONArray("productsTransactions");

                        final String resOrderId = transactions.getString("invoiceNumber");
                        final String resCreatedAt = transactions.getString("createdAt");

                        runOnUiThread(() -> {
                            LinearLayout LayoutOrderList = findViewById(R.id.LayoutOrderList);
                            ProgressBar ProgressOrderList = findViewById(R.id.LayoutOrderListProgress);
                            LinearLayout CardLayout = new LinearLayout(OrderActivity.this);
                            LinearLayout.LayoutParams ParamsCardLayout = new LinearLayout.
                                    LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                            CardLayout.setVisibility(TextView.GONE);
                            ProgressOrderList.setVisibility(TextView.VISIBLE);

                            CardLayout.setOrientation(LinearLayout.VERTICAL);
                            ParamsCardLayout.setMargins(0,0,0,dp(24));
                            CardLayout.setLayoutParams(ParamsCardLayout);
                            CardLayout.setPadding(0,0,0,dp(6));
                            CardLayout.setBackground(ContextCompat.getDrawable(OrderActivity.this, R.drawable.border_bottom));

                            TextView TextOrderId = new TextView(OrderActivity.this);
                            TextView TextDateOrder = new TextView(OrderActivity.this);

                            String dateHasFormatted = Instant.parse(resCreatedAt).atZone(ZoneId.systemDefault())
                                    .format(DateTimeFormatter.ofPattern("dd MMM yyy HH:mm"));

                            TextOrderId.setText("Order ID : " + resOrderId);
                            TextDateOrder.setText("Date : " + dateHasFormatted);

                            TextOrderId.setTextColor(ContextCompat.getColor(OrderActivity.this,R.color.black));
                            TextDateOrder.setTextColor(ContextCompat.getColor(OrderActivity.this,R.color.black));

                            TextOrderId.setTypeface(ResourcesCompat.getFont(OrderActivity.this,R.font.inter_medium));
                            TextDateOrder.setTypeface(ResourcesCompat.getFont(OrderActivity.this,R.font.inter_medium));

                            TextOrderId.setTextSize(14);
                            TextDateOrder.setTextSize(14);

                            CardLayout.addView(TextOrderId);
                            CardLayout.addView(TextDateOrder);

                            // Main Card Products
                            for (int j = 0; j < products.length() ; j++) {
                                try {
                                    JSONObject productTransaction = products.getJSONObject(j);
                                    JSONObject product = productTransaction.getJSONObject("productId");

                                    final String productTotalPrice = productTransaction.getString("totalPrice");
                                    final String productStatus = productTransaction.getString("transactionStatus");
                                    final String productQty = productTransaction.getString("productQuantity");
                                    final String productName = product.getString("productName");
                                    final String productImage = product.getString("productImage");

                                    TextView TextProductName = new TextView(OrderActivity.this);
                                    TextView TextProductTotalPrice = new TextView(OrderActivity.this);
                                    TextView TextProductQty = new TextView(OrderActivity.this);
                                    TextView TextProductStatus = new TextView(OrderActivity.this);

                                    ImageView ImageProduct= new ImageView(OrderActivity.this);

                                    LinearLayout ProductCardLayout = new LinearLayout(OrderActivity.this);
                                    LinearLayout ProductDetailsLayout = new LinearLayout(OrderActivity.this);
                                    LinearLayout ProductInfoLayout = new LinearLayout(OrderActivity.this);

                                    Glide.with(OrderActivity.this)
                                            .load(productImage)
                                            .into(ImageProduct);
                                    TextProductName.setText(productName);
                                    TextProductQty.setText("Qty : " + productQty );
                                    TextProductTotalPrice.setText(new FormatCurrency().Get(Integer.parseInt(productTotalPrice)));
                                    TextProductStatus.setText(productStatus);

                                    TextProductName.setTextSize(16);
                                    TextProductQty.setTextSize(12);
                                    TextProductStatus.setTextSize(14);
                                    TextProductTotalPrice.setTextSize(14);

                                    TextProductStatus.setTypeface(ResourcesCompat.getFont(OrderActivity.this,R.font.inter_semibold));
                                    TextProductStatus.setPadding(dp(6),dp(6),dp(6),dp(6));
                                    TextProductStatus.setTextAlignment(TextView.TEXT_ALIGNMENT_TEXT_START);

                                    TextProductTotalPrice.setTextColor(ContextCompat.getColor(OrderActivity.this,R.color.black));
                                    TextProductQty.setTextColor(ContextCompat.getColor(OrderActivity.this,R.color.black));
                                    TextProductName.setTextColor(ContextCompat.getColor(OrderActivity.this,R.color.black));

                                    if(productStatus.equals("PENDING")) {
                                        TextProductStatus.setTextColor(ContextCompat.getColor(OrderActivity.this,R.color.warning));
                                    } else if (productStatus.equals("COMPLETED")) {
                                        TextProductStatus.setTextColor(ContextCompat.getColor(OrderActivity.this,R.color.primary));
                                    } else {
                                        TextProductStatus.setTextColor(ContextCompat.getColor(OrderActivity.this,R.color.error));
                                    }

                                    // Create Layout Params
                                    LinearLayout.LayoutParams ParamsProductCardLayout = new LinearLayout
                                            .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                    LinearLayout.LayoutParams ParamsProductDetailsLayout = new LinearLayout
                                            .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                    LinearLayout.LayoutParams ParamsProductInfoLayout = new LinearLayout
                                            .LayoutParams(dp(0),ViewGroup.LayoutParams.WRAP_CONTENT,1);

                                    LinearLayout.LayoutParams ParamsImageProduct = new LinearLayout
                                            .LayoutParams(dp(48),dp(48));

                                    LinearLayout.LayoutParams ParamsTextProductName = new LinearLayout
                                            .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                    LinearLayout.LayoutParams ParamsTextProductTotalPrice = new LinearLayout
                                            .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                    LinearLayout.LayoutParams ParamsTextProductStatus = new LinearLayout
                                            .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);


                                    // Set Configuration Layout
                                    ProductCardLayout.setOrientation(LinearLayout.VERTICAL);
                                    ProductDetailsLayout.setOrientation(LinearLayout.HORIZONTAL);
                                    ProductInfoLayout.setOrientation(LinearLayout.VERTICAL);
                                    ProductDetailsLayout.setBackground(ContextCompat.getDrawable(OrderActivity.this, R.drawable.border_bottom));
                                    ProductCardLayout.setBackground(ContextCompat.getDrawable(OrderActivity.this, R.drawable.input_border));

                                    ProductDetailsLayout.setGravity(Gravity.START);
                                    ParamsImageProduct.gravity = Gravity.CENTER;

                                    ProductDetailsLayout.setPadding(dp(6),0,dp(6),0);
                                    ProductDetailsLayout.setPadding(0,dp(6),0,dp(6));

                                    ParamsProductCardLayout.setMargins(dp(6),dp(6),dp(6),dp(6));

                                    ProductInfoLayout.setPadding(dp(8),0,dp(8),0);
                                    ParamsProductInfoLayout.setMargins(dp(4),0,dp(4),0);

                                    // Set Params To Layout
                                    ProductCardLayout.setLayoutParams(ParamsProductCardLayout);
                                    ProductDetailsLayout.setLayoutParams(ParamsProductDetailsLayout);
                                    ProductInfoLayout.setLayoutParams(ParamsProductInfoLayout);
                                    ImageProduct.setLayoutParams(ParamsImageProduct);

                                    TextProductStatus.setLayoutParams(ParamsTextProductStatus);
                                    TextProductName.setLayoutParams(ParamsTextProductName);
                                    TextProductTotalPrice.setLayoutParams(ParamsTextProductTotalPrice);

                                    ProductCardLayout.addView(ProductDetailsLayout);
                                    ProductCardLayout.addView(TextProductStatus);

                                    ProductDetailsLayout.addView(ImageProduct);
                                    ProductDetailsLayout.addView(ProductInfoLayout);
                                    ProductDetailsLayout.addView(TextProductTotalPrice);

                                    ProductInfoLayout.addView(TextProductName);
                                    ProductInfoLayout.addView(TextProductQty);

                                    CardLayout.addView(ProductCardLayout);

                                    CardLayout.setVisibility(TextView.VISIBLE);
                                    ProgressOrderList.setVisibility(TextView.GONE);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            LayoutOrderList.addView(CardLayout);

                        });

                        Log.d("ORDERACTIVITY", transactions.getString("invoiceNumber"));
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        });
    }
}
