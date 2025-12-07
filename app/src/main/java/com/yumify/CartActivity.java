package com.yumify;


import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class CartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Integration to Layout by Id
        Button buttonNextPayment = (Button) findViewById(R.id.buttonNextPayment);
        Spinner selectionPayment = (Spinner)findViewById(R.id.selectionPayment);
        ImageView removeIcon = (ImageView)findViewById(R.id.removeIcon);

        removeIcon.setOnClickListener(v -> {
            // Use Case when Remove Item Clicked.
        });
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
