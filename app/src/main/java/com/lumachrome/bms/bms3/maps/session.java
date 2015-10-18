package com.lumachrome.bms.bms3.maps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.lumachrome.bms.bms3.LoginActivity;

import java.util.HashMap;

/**
 * Created by Yanagi Hisato on 8/26/2015.
 */
public class session {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME= "Prajurit_Session";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_ID= "Id_Prajurit";
    public static final String KEY_NAMA = "Nama";
    public static final String KEY_ALAMAT = "Alamat";
    public static final String KEY_TTL= "TTL";

    // Constructor
    public session(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public boolean islogin(){
        //pref.getBoolean(IS_LOGIN,false);
        return pref.getBoolean(IS_LOGIN,false);
    }
    public void createLoginSession(String id, String nama, String alamat, String ttl){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_NAMA, nama);
        editor.putString(KEY_ALAMAT, alamat);
        editor.putString(KEY_TTL, ttl);
        editor.commit();
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }

    }

    public void logoutUser(){
        editor.clear();
        editor.commit();
        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_ID, pref.getString(KEY_ID, null));
        user.put(KEY_NAMA, pref.getString(KEY_NAMA, null));
        user.put(KEY_ALAMAT, pref.getString(KEY_ALAMAT, null));
        user.put(KEY_TTL, pref.getString(KEY_TTL, null));
        return user;
    }
}
