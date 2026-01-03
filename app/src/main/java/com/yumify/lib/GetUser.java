package com.yumify.lib;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public class GetUser {

    private SharedPreferences prefs;
    public GetUser(Context context) {
        // Get User from Preferences
        prefs = context.getSharedPreferences("APP_AUTH", Context.MODE_PRIVATE);
    }

    public JSONObject Load() {
        String savedJson = prefs.getString("user", null);

        try {
            JSONObject user = new JSONObject(savedJson);
            return user.getJSONObject("data");
        }catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

}
