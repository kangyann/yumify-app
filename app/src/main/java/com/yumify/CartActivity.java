package com.yumify;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.yumify.lib.FormatCurrency;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CartActivity extends AppCompatActivity {
    private String userId = "";
    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
    private void AddProductsCart(SharedPreferences prefs) {
        JSONArray products;
        String productsString = prefs.getString("products", "[]");

        LinearLayout productLayout = findViewById(R.id.productLayout);
        TextView totalPrice = findViewById(R.id.cartTotalPrice);
        LinearLayout checkOutLayout = findViewById(R.id.checkOutLayout);
        productLayout.removeAllViews();
        try {
            products = new JSONArray(productsString);
            if(products.length() < 1) {
                TextView notFoundText = new TextView(CartActivity.this);
                LinearLayout notFoundLayout = new LinearLayout(CartActivity.this);

                LinearLayout.LayoutParams notFoundLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,dp(572));
                LinearLayout.LayoutParams notFoundTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                notFoundText.setLayoutParams(notFoundTextParams);
                notFoundLayout.setLayoutParams(notFoundLayoutParams);

                notFoundLayout.setGravity(Gravity.CENTER);
                notFoundLayout.setOrientation(LinearLayout.VERTICAL);

                notFoundText.setTextSize(18);
                notFoundLayout.addView(notFoundText);
                notFoundText.setText("Tidak ada pesanan.");
                notFoundText.setTypeface(ResourcesCompat.getFont(CartActivity.this,R.font.inter_semibold));
                notFoundText.setTextColor(ContextCompat.getColor(CartActivity.this,R.color.black));

                productLayout.addView(notFoundLayout);

                checkOutLayout.setVisibility(View.GONE);
                return;
            }
            int prices = 0;
            for (int i = 0; i < products.length(); i++) {
                JSONObject product = products.getJSONObject(i);
                String productId = product.getString("productId");
                String productQty = product.getString("productQty");
                String productName = product.getString("productName");
                String productImage = product.getString("productImage");
                String productPrice = product.getString("productPrice");

                int priceXQty = Integer.parseInt(productPrice) * Integer.parseInt(productQty);
                prices = prices + priceXQty;

                LinearLayout newProductLayouts = new LinearLayout(CartActivity.this);
                LinearLayout.LayoutParams paramsNewProductLayout = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);


                newProductLayouts.setOrientation(LinearLayout.HORIZONTAL);
                newProductLayouts.setPadding(dp(24),dp(14),dp(24),dp(14));
                newProductLayouts.setLayoutParams(paramsNewProductLayout);
                newProductLayouts.setBackground(ContextCompat.getDrawable(CartActivity.this,R.drawable.border_bottom));

                ImageView newProductImage = new ImageView(CartActivity.this);
                LinearLayout.LayoutParams paramsProductImage = new LinearLayout.LayoutParams(dp(64),dp(64));
                TextView newIndex = new TextView(CartActivity.this);
                int index = i + 1;
                newIndex.setText(index + ".");
                newIndex.setTextColor(ContextCompat.getColor(CartActivity.this,R.color.black));
                newIndex.setTypeface(ResourcesCompat.getFont(CartActivity.this,R.font.inter_semibold));

                newIndex.setPadding(0,0,dp(8),0);
                Glide.with(CartActivity.this)
                        .load(productImage)
                        .into(newProductImage);
                newProductImage.setLayoutParams(paramsProductImage);

                newProductLayouts.addView(newIndex);
                newProductLayouts.addView(newProductImage);

                TextView newProductName = new TextView(CartActivity.this);
                TextView newProductPrice = new TextView(CartActivity.this);
                TextView newProductQty = new TextView(CartActivity.this);

                newProductName.setText(productName);
                newProductName.setTextSize(18);
                newProductName.setTextColor(ContextCompat.getColor(CartActivity.this,R.color.primary));
                newProductName.setTypeface(ResourcesCompat.getFont(CartActivity.this,R.font.inter_semibold));

                newProductPrice.setText(new FormatCurrency().Get(Integer.parseInt(productPrice)));
                newProductPrice.setTextColor(ContextCompat.getColor(CartActivity.this,R.color.primary));

                newProductQty.setText(String.format("Qty : " + productQty));
                newProductQty.setTextColor(ContextCompat.getColor(CartActivity.this,R.color.primary));

                LinearLayout productDescLayout = new LinearLayout(CartActivity.this);
                LinearLayout.LayoutParams paramsProductDescLayout = new LinearLayout.LayoutParams(
                        dp(0),
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1);

                paramsProductDescLayout.gravity = Gravity.CENTER_VERTICAL;
                paramsProductDescLayout.setMarginStart(dp(8));
                productDescLayout.setLayoutParams(paramsProductDescLayout);
                productDescLayout.setOrientation(LinearLayout.VERTICAL);
                productDescLayout.addView(newProductName);
                productDescLayout.addView(newProductPrice);
                productDescLayout.addView(newProductQty);

                ImageView newRemoveIcon = new ImageView(CartActivity.this);
                LinearLayout.LayoutParams paramsRemoveIcon = new LinearLayout.LayoutParams(dp(24),dp(24));
                paramsRemoveIcon.gravity = Gravity.BOTTOM;

                newRemoveIcon.setLayoutParams(paramsRemoveIcon);
                newRemoveIcon.setImageResource(R.drawable.removeicon);

                newProductLayouts.addView(productDescLayout);
                newProductLayouts.addView(newRemoveIcon);
                productLayout.addView(newProductLayouts);

                //
                newRemoveIcon.setOnClickListener(v -> {
                    RemoveProduct(prefs, productId);
                });
                totalPrice.setText(String.format("Total Purchase : " + new FormatCurrency().Get(prices)));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void RemoveProduct(SharedPreferences prefs, String productId){
        String productsString = prefs.getString("products", "[]");
        try {
            JSONArray products = new JSONArray(productsString);
            JSONArray newArrayProducts = new JSONArray();
            for (int j = 0; j < products.length(); j++) {
                JSONObject obj = products.getJSONObject(j);
                if(!obj.getString("productId").equals(productId)){
                    newArrayProducts.put(obj);
                }
            }
            prefs.edit().putString("products", newArrayProducts.toString()).apply();
            AddProductsCart(prefs);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    private void ClickPaymentButton (SharedPreferences prefs,JSONArray products) {
        OkHttpClient client = new OkHttpClient();
        JSONObject bodyRequest = new JSONObject();
        JSONArray newRequestProduct = new JSONArray();
        int totalPriceAll = 0;
        for (int i = 0; i < products.length(); i++) {
            try {
                JSONObject c = products.getJSONObject(i);
                int qty = c.getInt("productQty");
                int totalPrice = c.getInt("productQty") * c.getInt("productPrice");
                totalPriceAll = totalPriceAll + totalPrice;
                JSONObject n = new JSONObject();

                n.put("productName", c.getString("productName"));
                n.put("productQty", qty);
                n.put("totalPrice", totalPrice);

                newRequestProduct.put(n);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
        try {
            bodyRequest.put("paymentName", "QRIS");
            bodyRequest.put("userId", Integer.parseInt(userId));
            bodyRequest.put("totalPriceAll",totalPriceAll);
            bodyRequest.put("productData", newRequestProduct);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        RequestBody dataRequest = RequestBody.create(bodyRequest.toString(),
                MediaType.parse("application/json; charset=utf-8"));
        Log.d("TRANSACTIONS : ",bodyRequest.toString());
        Request request = new Request.Builder()
                .url("https://yumify-api.vercel.app/api/transaction")
                .post(dataRequest)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("TRANSACTIONS : ","ERROR FETCHING TRANSACTIONS." + e);
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String body = response.body().string();
                if(response.code() != 200) {
                    Log.d("TRANSACTIONS : ","ERROR" + body);
                    return;
                }
                Log.d("TRANSACTIONS : ",body);
                try {
                    JSONObject responseJson = new JSONObject(body);
                    String message = responseJson.getString("message");
                    JSONObject data = responseJson.getJSONObject("data");
                    String invoiceNumber = data.getString("invoiceNumber");
                    runOnUiThread(() -> {
                        Toast.makeText(CartActivity.this, message, Toast.LENGTH_SHORT).show();
                    });
                    prefs.edit().remove("products").apply();
                    Intent intent = new Intent(CartActivity.this,PaymentActivity.class);
                    intent.putExtra("invoiceNumber", invoiceNumber);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        // Integration to Layout by Id
        Button buttonNextPayment = findViewById(R.id.buttonNextPayment);
        Spinner selectionPayment = findViewById(R.id.selectionPayment);

        SharedPreferences prefsCart = getSharedPreferences("APP_CART", MODE_PRIVATE);
        String currentProducts = prefsCart.getString("products", "[]");

        AddProductsCart(prefsCart);
        LoadSelection(selectionPayment);

        buttonNextPayment.setOnClickListener(v -> {
            try {
                ClickPaymentButton(prefsCart, new JSONArray(currentProducts));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public void LoadSelection(Spinner selection) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url("https://yumify-api.vercel.app/api/payments").build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("PAYMENTS","ERROR FETCHING PAYMENTS." + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String body = response.body().string();
                try {
                    JSONObject responseJson = new JSONObject(body);

                    JSONArray data = responseJson.getJSONArray("data");
                    List<String> listPayment = new ArrayList<>();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject obj = data.getJSONObject(i);
                        listPayment.add(obj.getString("paymentName"));
                    }
                    runOnUiThread(() -> {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                CartActivity.this,
                                android.R.layout.simple_spinner_item,
                                listPayment
                        ){
                            @NonNull
                            @Override
                            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                TextView txt = (TextView)super.getDropDownView(position, convertView, parent);
                                txt.setTextColor(ContextCompat.getColor(CartActivity.this,R.color.black));
                                return txt;
                            }

                            @Override
                            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                TextView txt = (TextView)super.getDropDownView(position, convertView, parent);
                                txt.setTextColor(Color.DKGRAY);
                                return txt;
                            }
                        };

                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        selection.setAdapter(adapter);
                    });

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }
}
