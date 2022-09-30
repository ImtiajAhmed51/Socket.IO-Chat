package com.example.chatsocketio;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class preferences {
    private static final String DATA_LOGIN = "status_login",
            DATA_NAME = "name",DATA_IMAGE="image",DATA_ID="id";
    private static SharedPreferences getSharedPreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
    public static void setDataAs(Context context,String id, String name,String image){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(DATA_ID,id);
        editor.putString(DATA_NAME,name);
        editor.putString(DATA_IMAGE,image);
        editor.apply();
    }
    public static String getDataId(Context context){
        return getSharedPreferences(context).getString(DATA_ID,"");
    }
    public static String getDataName(Context context){
        return getSharedPreferences(context).getString(DATA_NAME,"");
    }
    public static String getDataImage(Context context){
        return getSharedPreferences(context).getString(DATA_IMAGE,"");
    }
    public static void setDataLogin(Context context, boolean status){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(DATA_LOGIN,status);
        editor.apply();
    }
    public static boolean getDataLogin(Context context){
        return getSharedPreferences(context).getBoolean(DATA_LOGIN,false);
    }
    public static void clearData(Context context){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(DATA_ID);
        editor.remove(DATA_NAME);
        editor.remove(DATA_IMAGE);
        editor.remove(DATA_LOGIN);
        editor.apply();
    }
}