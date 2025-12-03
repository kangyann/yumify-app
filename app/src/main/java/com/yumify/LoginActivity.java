package com.yumify;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.*;


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.buttonLogin).setOnClickListener(v -> {
            EditText userText = findViewById(R.id.InputUsername);
            EditText passText = findViewById(R.id.inputPassword);

            String get_user_value = userText.getText().toString();
            String get_pass_value = passText.getText().toString();

            if (get_user_value.isEmpty()  || get_pass_value.isEmpty()) {
                Toast.makeText(this, "Username dan Password dibutuhkan!.", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = getSharedPreferences("APP_LANGUAGE", MODE_PRIVATE);
            String language = prefs.getString("lang", null);

            LoginApi(get_user_value,get_pass_value,language);
        });
    }

    private void LoginApi(String username, String password,String language) {

        OkHttpClient client = new OkHttpClient();

        RequestBody dataRequest = new FormBody.Builder()
                .add("username",username)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url("https://yumify-api.vercel.app/api/auth?lang=" + language)
                .post(dataRequest)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("API_ERROR", "Error: " + e.getMessage());
                runOnUiThread(() ->
                        Toast.makeText(LoginActivity.this, "Internal Server Error!", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String result = response.body().string();
                String responseText;
                try {
                    JSONObject convertJson = new JSONObject(result);
                    responseText = convertJson.getString("message");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                runOnUiThread(() ->
                        Toast.makeText(LoginActivity.this, responseText, Toast.LENGTH_SHORT).show()) ;
                if (response.code() == 200) {
                    Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }
}