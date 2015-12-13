package com.example.henneberg.shittyapp.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Henneberg on 23-11-2015.
 */
public class AppConstants extends AppCompatActivity {

    public static final String BRACELET_ADDRESS = "DD:7D:B3:58:CA:98";

    public static final String FINGERPRINT_PATH = "/fingerprint/";
    public static final String EXPERIMENT_PATH = "/experiment/";


    private static AppConstants INSTANCE;
    private static Context CONTEXT;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private static final String PREFSNAME = "CattivityPreferences";

    // KEYS for Preferences
    private static final String _serverAddress = "ServerAddress";
    private static final String _phoneName = "PhoneName";
    private static final String _fingRun = "FingerprintingRun";
    private static final String _maxTimeDiscovery = "MaxTimeDiscovery";

    // VALUES for Preferences
    private static String SERVER_ADDRESS = "95.85.2.220:8080";
    private static String PHONE_NAME = "-1";
    private static int FINGERPRINTING_RUN = 2;
    private static long MAXTIME = 25000;


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

    public static int getFingerprintingRun() {
        return getPrefs().getInt(_fingRun, FINGERPRINTING_RUN);
    }

    public static long getMaxTime() {
        return getPrefs().getLong(_maxTimeDiscovery, MAXTIME);
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

    public static void setFingerprintingRun(int run) {
        getEditor().putInt(_fingRun, run);
        FINGERPRINTING_RUN = run;

        commitChanges();
    }

    public static void setMaxTime(long time ) {
        getEditor().putLong(_maxTimeDiscovery, time);

        commitChanges();
    }



    private static void commitChanges() {
        getEditor().commit();
    }



    public static JSONObject createJSON_Experiment(long timestamp, Short RSSI) {
        JSONObject obj = new JSONObject();

        try {
            obj.put("timestamp", timestamp);
            obj.put("value", RSSI);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj;
    }

    public static void setContext(Context ctx) {
        CONTEXT = ctx;
    }

}
