package com.example.henneberg.shittyapp.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Henneberg on 23-11-2015.
 */
public class AppConstants extends AppCompatActivity {

    private static AppConstants INSTANCE;
    private static Context CONTEXT;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public static final String PREFSNAME = "CattivityPreferences";
    public static final String BRACELET_ADDRESS = "DD:7D:B3:58:CA:98";

    // KEYS for Preferences
    private static final String _serverAddress = "ServerAddress";
    private static final String _phoneName = "PhoneName";

    // VALUES for Preferences
    private static String SERVER_ADDRESS = "95.85.2.220:8080";
    private static String PHONE_NAME = "D046??";


    private AppConstants() {
        prefs = CONTEXT.getSharedPreferences(PREFSNAME, 0);
        editor = prefs.edit();
    }

    private static AppConstants getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new AppConstants();
        }

        return INSTANCE;
    }

    private static SharedPreferences getPrefs() {
        return getInstance().prefs;
    }

    private static SharedPreferences.Editor getEditor() {
        return getInstance().editor;
    }



    public static String getServerAddress() {
        return getPrefs().getString(_serverAddress, SERVER_ADDRESS);
    }

    public static String getPhoneName() {
        return getPrefs().getString(_phoneName, PHONE_NAME);
    }

    private static void commitChanges() {
        getEditor().commit();
    }



    public static void setServerAddress(String addr) {
        getEditor().putString(_serverAddress, addr);
        SERVER_ADDRESS = addr;

        commitChanges();
    }

    public static void setPhoneName(String name) {
        getEditor().putString(_phoneName, name);
        PHONE_NAME = name;

        commitChanges();
    }






    public static void setContext(Context ctx) {
        CONTEXT = ctx;
    }

}
