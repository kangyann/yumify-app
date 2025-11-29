package com.yumify;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    /* CustomFlagCountry -> Replace a Text Location & a Icon Location Image */
    private void CustomFlagCountry(String country, ImageView imageViewId,TextView flagImageView){
        int c; String t;

        switch (country.toLowerCase()){
            case "id":
                c = R.drawable.indonesia;
                t = "Indonesia";
                break;
            case "us":
                c = R.drawable.united_states;
                t = "United States";
                break;
            case "ru":
                c = R.drawable.rusia;
                t = "Rusia";
                break;
            case "ch":
                c = R.drawable.switzerland;
                t = "Switzerland";
                break;
            default:
                c = R.drawable.indonesia;
                t = "Indonesia";
                break;
        }
            /* SET */
            imageViewId.setImageResource(c);
            flagImageView.setText(t);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        /* Custom Flag and Language by Country */
        String country = Locale.getDefault().getCountry();
        CustomFlagCountry(country, findViewById(R.id.iconLocation),findViewById(R.id.textLocation));

        /* Delay by Times as a SplashScreen */
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        },3000);
    }

}