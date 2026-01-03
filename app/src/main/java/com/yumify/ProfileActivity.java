package com.yumify;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yumify.lib.GetUser;

import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        LinearLayout buttonOrder = findViewById(R.id.buttonOrder);
        TextView ProfileUsername = findViewById(R.id.ProfileUsername);
        Button buttonLogout = findViewById(R.id.buttonLogout);
        Button buttonBackToHome = findViewById(R.id.buttonBackToHome);

        GetUser getUser = new GetUser(ProfileActivity.this);
        JSONObject user = getUser.Load();
        String username = user.optString("username");
        ProfileUsername.setText(username);
        // Event Button Order List
        buttonOrder.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this,OrderActivity.class);
            startActivity(intent);
        });

        // Event Button Back to Home
        buttonBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this,HomeActivity.class);
            startActivity(intent);
        });
        // Event Button Logout
        buttonLogout.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            SharedPreferences prefs = getSharedPreferences("APP_AUTH", MODE_PRIVATE);
            prefs.edit().clear().apply();
            Toast.makeText(ProfileActivity.this,"Logout berhasil", Toast.LENGTH_SHORT).show();
            startActivity(intent);
            finish();
        });
    }
}
