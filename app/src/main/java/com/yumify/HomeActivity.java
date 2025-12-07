package com.yumify;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.gridlayout.widget.GridLayout;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView varUser = findViewById(R.id.variable_user);
        ImageView profileIcon = findViewById(R.id.profileIcon);
        ImageView cartIcon = findViewById(R.id.cartIcon);
        LinearLayout popupAddToCart = findViewById(R.id.popupAddToCart);
        ConstraintLayout Home = findViewById(R.id.home);

        cartIcon.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this,CartActivity.class);
            startActivity(intent);
        });
        profileIcon.setOnClickListener(v -> {
            Log.d("HOMEPAGE","Profile Clicked");
            //Redirect to Profile page
        });

        getProduct();
        JSONObject user = LoadUser();
        varUser.setText(user.optString("username"));
        Home.setOnClickListener(v -> {
            if(popupAddToCart.getVisibility() == View.VISIBLE) {
                popupAddToCart.setVisibility(View.GONE);
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

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String result = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray products = jsonObject.optJSONArray("data");
                    if (products == null) {
                        Log.d("PRODUCTS", "PRODUCT TIDAK ADA");
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

                                LinearLayout linear = new LinearLayout(HomeActivity.this);
                                linear.setOrientation(LinearLayout.VERTICAL);
                                linear.setGravity(Gravity.CENTER);

                                GridLayout.LayoutParams grid = new GridLayout.LayoutParams();
                                grid.width = 0;     // px
                                grid.setMargins(16, 16, 16, 16);
                                grid.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);

                                linear.setLayoutParams(grid);

                                ImageView image = new ImageView(HomeActivity.this);
                                LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(256,256);
                                image.setLayoutParams(imgParams);
                                image.setScaleType(ImageView.ScaleType.CENTER_CROP);

                                Glide.with(HomeActivity.this)
                                        .load(product.optString("productImage"))
                                        .into(image);

                                TextView textImage = new TextView(HomeActivity.this);

                                textImage.setText(product.optString("productName"));
                                textImage.setGravity(Gravity.CENTER);
                                textImage.setTextSize(12);
                                textImage.setTypeface(ResourcesCompat.getFont(HomeActivity.this,R.font.inter_semibold));
                                textImage.setPadding(0,4,0,0);

                                linear.addView(image);
                                linear.addView(textImage);
                                linear.setId(LinearLayout.generateViewId());
                                linear.setClickable(true);
                                linear.setOnClickListener( v -> {
                                    // Show the popup when product clicked.
                                    popupAddToCart.setVisibility(View.VISIBLE);
                                    Log.d("PRODUCT CLICKED", String.valueOf(linear.getId()));
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
        SharedPreferences prefs = getSharedPreferences("APP_AUTH", MODE_PRIVATE);
        String savedJson = prefs.getString("user", null);
        try {
            JSONObject user = new JSONObject(savedJson);
            return user.optJSONObject("data");
        }catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
