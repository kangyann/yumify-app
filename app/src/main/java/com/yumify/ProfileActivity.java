package com.yumify;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        LinearLayout buttonOrder = findViewById(R.id.buttonOrder);

        buttonOrder.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this,OrderActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
