package com.saravana.containmentzonealertapp;

import android.content.Context;
import android.content.SharedPreferences;

class AuthUtils {
    public static final String TOKENS_PREF = "tokens" ;
    public static final String AUTH_TOKEN_KEY = "jwt";

    public static void storeAuthorizationToken(Context context, String token){
        SharedPreferences sharedPreferences = context.getSharedPreferences(TOKENS_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AUTH_TOKEN_KEY,"Bearer "+token);
        editor.apply();
    }
    public static String getAuthorizationToken(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(TOKENS_PREF,Context.MODE_PRIVATE);
        return sharedPreferences.getString(AUTH_TOKEN_KEY,"");
    }
}
