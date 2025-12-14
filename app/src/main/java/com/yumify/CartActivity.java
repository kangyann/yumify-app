package com.yumify;


import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.OnNewIntentProvider;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.yumify.lib.FormatCurrency;

import org.json.JSONArray;
import org.json.JSONObject;

public class CartActivity extends AppCompatActivity {
    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Integration to Layout by Id
        Button buttonNextPayment = findViewById(R.id.buttonNextPayment);
        Spinner selectionPayment = findViewById(R.id.selectionPayment);
        TextView totalPrice = findViewById(R.id.cartTotalPrice);
        LinearLayout productLayout = findViewById(R.id.productLayout);

        SharedPreferences prefs = getSharedPreferences("APP_CART", MODE_PRIVATE);
        String productsString = prefs.getString("products", "[]");

        try {
            JSONArray products = new JSONArray(productsString);
            int prices = 0;
            Log.d("CART_ACTIVITY", products.toString());
            for (int i = 0; i < products.length(); i++) {
                JSONObject product = products.getJSONObject(i);
                String productName = product.optString("productName");
                String productImage = product.optString("productImage");
                String productId = product.optString("productId");
                String id = product.optString("id");
                String productPrice = product.optString("productPrice");
                String productQty = product.optString("productQty");
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
                newIndex.setText(id + ".");
                newIndex.setTypeface(ResourcesCompat.getFont(CartActivity.this,R.font.inter_regular));

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
                newProductName.setTypeface(ResourcesCompat.getFont(CartActivity.this,R.font.inter_semibold));

                newProductPrice.setText(new FormatCurrency().Get(Integer.parseInt(productPrice)));

                newProductQty.setText(String.format("Qty : " + productQty));

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

                newRemoveIcon.setOnClickListener(v -> {
                    // Need required remove for one product.
                });
                totalPrice.setText(String.format("Total Purchase : " + new FormatCurrency().Get(prices)));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        buttonNextPayment.setOnClickListener(v -> {
            // Redirect to payment page.
        });

        // Load Selection
        LoadSelection(selectionPayment);
    }


    public void LoadSelection(Spinner selection) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                CartActivity.this,
                R.array.cart_selection_payment,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selection.setAdapter(adapter);
    }
}
