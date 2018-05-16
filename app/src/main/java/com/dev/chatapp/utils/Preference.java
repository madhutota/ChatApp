package com.dev.chatapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;

public class Preference {
    private static final String APP_CONFIG_PREF = "APP_CONFIG_PREF";
    public static final String DEVICE_TYPE = "android";




    /*SET THE BOOLEAN  SHARED PREF DATA*/
    public static void setPrefBooleanData(Context context, String key, boolean value) {
        SharedPreferences appInstallInfoSharedPref = context.
                getSharedPreferences(APP_CONFIG_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor appInstallInfoEditor = appInstallInfoSharedPref.edit();
        appInstallInfoEditor.putBoolean(key, value);
        appInstallInfoEditor.apply();
    }


    /*GET THE BOOLEAN SHARED PREF DATA*/
    public static Boolean getPrefBooleanData(Context context, String key) {
        try {
            SharedPreferences pref = context.getSharedPreferences(APP_CONFIG_PREF, Context.MODE_PRIVATE);
            return pref.getBoolean(key, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /*SET THE STRING SHARED PREF DATA*/
    public static void setPrefStringData(Context context, String key, String value) {
        SharedPreferences appInstallInfoSharedPref = context.
                getSharedPreferences(APP_CONFIG_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor appInstallInfoEditor = appInstallInfoSharedPref.edit();
        appInstallInfoEditor.putString(key, value);
        appInstallInfoEditor.apply();
    }


    /*GET THE STRING SHARED PREF DATA*/
    public static String getPrefStringData(Context context, String key) {
        try {
            SharedPreferences pref = context.getSharedPreferences(APP_CONFIG_PREF, Context.MODE_PRIVATE);
            return pref.getString(key, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


}
