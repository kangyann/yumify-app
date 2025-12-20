package com.yumify;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.gridlayout.widget.GridLayout;

import com.bumptech.glide.Glide;
import com.yumify.lib.FormatCurrency;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        TextView cartIconItem = findViewById(R.id.cartIcon_item);
        LoadCartItem(cartIconItem);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Get Component from activity_home.xml by ID
        TextView labelUsername = findViewById(R.id.variable_user);
        ImageView profileIcon = findViewById(R.id.profileIcon);
        ImageView cartIcon = findViewById(R.id.cartIcon);

        LinearLayout popupAddToCart = findViewById(R.id.popupAddToCart);
        popupAddToCart.bringToFront();
        ConstraintLayout Home = findViewById(R.id.home);

        // Event Clicked for cartIcon.
        cartIcon.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this,CartActivity.class);
            startActivity(intent);
        });

        // Event Clicked for profileIcon.
        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this,ProfileActivity.class);
            startActivity(intent);
        });

        // Get Products
        getProduct();
//        getPayments();

        // Get User
        JSONObject user = LoadUser();
        labelUsername.setText(user.optString("username"));


        // Event Clicked to Visibility Popup. [FINAL]
        Home.setOnClickListener(v -> {
            if(popupAddToCart.getVisibility() == View.VISIBLE) {
                popupAddToCart.setVisibility(View.GONE);
            }
        });

    }
    public void LoadCartItem(TextView item) {
        SharedPreferences prefs = getSharedPreferences("APP_CART", MODE_PRIVATE);
        String cart = prefs.getString("products", "[]");
        Log.d("HOMEACTIVITY", cart);
        try {
            JSONArray products = new JSONArray(cart);
            if(products.length() > 0){
                item.setVisibility(View.VISIBLE);
                item.setText(String.valueOf(products.length()));
            } else {
                item.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    public void getPayments() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url("https://yumify-api.vercel.app/api/payments").build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("PAYMENTS", "ERROR FETCHING PAYMENTS." + e);
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
                    JSONArray paymentArr = new JSONArray(listPayment);
                    SharedPreferences prefsPayments = getSharedPreferences("APP_PAYMENTS", MODE_PRIVATE);
                    prefsPayments.edit().putString("payments",paymentArr.toString()).apply();

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void getProduct() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url("https://yumify-api.vercel.app/api/product")
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("PRODUCTS","ERROR FETCHING PRODUCTS.");
            }

            @SuppressLint("ResourceType")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String result = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray products = jsonObject.optJSONArray("data");

                    if (products == null) {
                        return;
                    }

                    runOnUiThread(() -> {
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        GridLayout gridLayoutMakanan = findViewById(R.id.gridLayoutMakanan);
                        GridLayout gridLayoutMinuman = findViewById(R.id.gridLayoutMinuman);
                        LinearLayout popupAddToCart = findViewById(R.id.popupAddToCart);

                        for (int i = 0; i < products.length(); i++) {
                            try {
                                JSONObject product = products.getJSONObject(i);

                                String optProductName = product.optString("productName");
                                String optProductImage = product.optString("productImage");
                                String optProductPrice = product.optString("productPrice");

                                LinearLayout linear = new LinearLayout(HomeActivity.this);
                                LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(256,256);
                                GridLayout.LayoutParams grid = new GridLayout.LayoutParams();
                                ImageView image = new ImageView(HomeActivity.this);
                                TextView textImage = new TextView(HomeActivity.this);

                                linear.setOrientation(LinearLayout.VERTICAL);
                                linear.setGravity(Gravity.CENTER);

                                grid.width = 0;
                                grid.setMargins(16, 16, 16, 16);
                                grid.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);

                                image.setLayoutParams(imgParams);
                                image.setScaleType(ImageView.ScaleType.CENTER_CROP);

                                Glide.with(HomeActivity.this)
                                        .load(optProductImage)
                                        .into(image);

                                textImage.setText(optProductName);
                                textImage.setGravity(Gravity.CENTER);
                                textImage.setTextSize(12);
                                textImage.setTextColor(ContextCompat.getColor(HomeActivity.this,R.color.black));
                                textImage.setTypeface(ResourcesCompat.getFont(HomeActivity.this,R.font.inter_semibold));
                                textImage.setPadding(0,6,0,6);

                                linear.setLayoutParams(grid);
                                linear.addView(image);
                                linear.addView(textImage);
                                linear.setId(LinearLayout.generateViewId());
                                linear.setClickable(true);

                                linear.setOnClickListener( v -> {
                                    TextView productName = findViewById(R.id.popup_productName);
                                    TextView productPrice = findViewById(R.id.popup_productPrice);
                                    TextView productQty = findViewById(R.id.popup_productQty);
                                    TextView totalPrice = findViewById(R.id.popup_totalPrice);
                                    TextView buttonMin = findViewById(R.id.buttonMin);
                                    TextView buttonPlus = findViewById(R.id.buttonPlus);
                                    ImageView productImage = findViewById(R.id.popup_productImage);
                                    Button addToCart = findViewById(R.id.popup_addToCart);
                                    TextView cartIconItem = findViewById(R.id.cartIcon_item);

                                    popupAddToCart.setVisibility(View.VISIBLE);

                                    Glide.with(HomeActivity.this)
                                            .load(optProductImage)
                                            .into(productImage);

                                    AtomicInteger qty = new AtomicInteger(1);
                                    AtomicInteger totalPriceProduct = new AtomicInteger(Integer.parseInt(optProductPrice));
                                    String currency = new FormatCurrency().Get(Integer.parseInt(optProductPrice));

                                    productName.setText(optProductName);
                                    productPrice.setText(currency);
                                    totalPrice.setText(String.format("Total Harga : " + currency));
                                    productQty.setText(String.format("Jumlah : " + qty.get()));

                                    buttonMin.setTextColor(ContextCompat.getColor(HomeActivity.this,R.color.white));
                                    buttonMin.setOnClickListener(btn_v -> {
                                        if(qty.get() > 1) {
                                            productQty.setText(String.format("Jumlah : " +  qty.decrementAndGet()));
                                            totalPrice.setText(String.format("Total Harga : " + new FormatCurrency().Get(totalPriceProduct.get() * qty.get())));;
                                        }
                                    });

                                    buttonPlus.setOnClickListener(btn_v -> {
                                        productQty.setText(String.format("Jumlah : " + qty.incrementAndGet()));
                                        totalPrice.setText(String.format("Total Harga : " + new FormatCurrency().Get(totalPriceProduct.get() * qty.get())));
                                    });

                                    addToCart.setOnClickListener(btn_v -> {
                                        SharedPreferences prefsCart = getSharedPreferences("APP_CART",MODE_PRIVATE);
                                        String savedCart = prefsCart.getString("products", "[]");

                                        boolean isExistOnCart = false;
                                        try {
                                            JSONArray cart = new JSONArray(savedCart);
                                            for (int j = 0; j < cart.length(); j++) {
                                                JSONObject productOnCart = cart.optJSONObject(j);

                                                if (productOnCart.optInt("productId") == product.optInt("id")){
                                                    int newQty = qty.get() + productOnCart.optInt("productQty");
                                                    productOnCart.put("productQty", newQty);
                                                    productOnCart.put("totalPrice", productOnCart.optInt("totalPrice") * newQty);
                                                    isExistOnCart = true;
                                                    break;
                                                }
                                            }

                                            int id = 1;
                                            if(cart.length() > 0) {
                                                id = cart.length() + 1;
                                            }
                                            if(!isExistOnCart) {
                                                JSONObject product_cart = new JSONObject();
                                                product_cart.put("id", id);
                                                product_cart.put("productId", product.optInt("id"));
                                                product_cart.put("productName", optProductName);
                                                product_cart.put("productPrice", totalPriceProduct.get());
                                                product_cart.put("productQty", qty.get());
                                                product_cart.put("productImage", optProductImage);
                                                product_cart.put("totalPrice", totalPriceProduct.get() * qty.get());

                                                cart.put(product_cart);
                                            }
                                            prefsCart.edit().putString("products", cart.toString()).apply();

                                            LoadCartItem(cartIconItem);

                                            popupAddToCart.setVisibility(View.GONE);


                                            Log.d("ADD_TO_CART","Successfully Added : " + optProductName);
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                        Log.d("ADD_TO_CART", "ProductName : " + optProductName);
                                    });
                                });

                                if (!"makanan".equalsIgnoreCase(product.optString("productType"))) {
                                    gridLayoutMinuman.addView(linear);
                                    continue;
                                }

                                gridLayoutMakanan.addView(linear);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                } catch (Exception e) {
                  Log.e("PRODUCTS", "EXCEPTION ERROR : " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        });
    }
    public JSONObject LoadUser() {
        // Get User from Preferences
        SharedPreferences prefs = getSharedPreferences("APP_AUTH", MODE_PRIVATE);
        String savedJson = prefs.getString("user", null);

        // Try Compile a Data to JSONObject.
        try {
            JSONObject user = new JSONObject(savedJson);
            return user.optJSONObject("data");
        }catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
