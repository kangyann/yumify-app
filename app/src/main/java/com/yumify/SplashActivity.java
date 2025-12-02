package com.yumify;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    /* CustomFlagCountry -> Replace a Text Location & a Icon Location Image */
    private void CustomFlagCountry(String country, ImageView imageViewId,TextView flagImageView){
        int c; String t; String lang;
        Log.d("COUNTRY", country);
        switch (country.toLowerCase()){
            case "id":
                c = R.drawable.indonesia;
                t = "Indonesia";
                lang = "id";
                break;
            case "us":
                c = R.drawable.united_states;
                t = "United States";
                lang = "en";
                break;
            case "ru":
                c = R.drawable.rusia;
                t = "Russia";
                lang = "ru";
                break;
            case "de":
                c = R.drawable.germany;
                t = "Germany";
                lang = "de";
                break;
            case "jp":
                c = R.drawable.japan;
                t = "Japan";
                lang = "jp";
                break;
            default:
                c = R.drawable.indonesia;
                t = "Indonesia";
                lang = "id";
                break;
        }
            /* SET */
            SharedPreferences prefs = getSharedPreferences("APP_LANGUAGE", MODE_PRIVATE);
            prefs.edit().putString("lang",lang).apply();

            imageViewId.setImageResource(c);
            flagImageView.setText(t);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        /* Custom Flag and Language by Country */
        String country = Locale.getDefault().getCountry();
        Log.d("COUNTY =>", country);
        CustomFlagCountry(country, findViewById(R.id.iconLocation),findViewById(R.id.textLocation));

        /* Delay by Times as a SplashScreen */
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        },3000);
    }

}