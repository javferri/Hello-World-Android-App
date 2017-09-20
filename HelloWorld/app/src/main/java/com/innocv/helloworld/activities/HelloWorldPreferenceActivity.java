package com.innocv.helloworld.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.innocv.helloworld.R;

/**
 * User preferences activity
 *
 * @author Javier Fernandez Riolobos
 * @version 1.0
 */
public class HelloWorldPreferenceActivity extends AppCompatActivity {


    /* User ID */
    public final static String USER_ID_KEY = "id";
    public final static String USER_ID_DEFAULT = "";

    /* Username */
    public final static String USERNAME_KEY = "username";
    public final static String USERNAME_DEFAULT = "";

    /* User's birthdate */
    public final static String BIRTHDATE_KEY = "birthdate";
    public final static String BIRTHDATE_DEFAULT = "";


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences_fragment);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        HelloWorldPreferenceFragment fragment = new HelloWorldPreferenceFragment();
        fragmentTransaction.replace(android.R.id.content, fragment);
        fragmentTransaction.commit();
    }

    /**
     * ID getter method
     * @param context context
     * @return user's id
     */
    public static String getID(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(USER_ID_KEY, USER_ID_DEFAULT);
    }

    /**
     * ID setter method
     * @param context context
     * @param ID user's id
     */
    public static void setID(Context context, String ID) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_ID_KEY, ID);
        editor.commit();
    }

    /**
     * Username getter method
     * @param context context
     * @return username
     */
    public static String getUserName(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(USERNAME_KEY, USERNAME_DEFAULT);
    }

    /**
     * Username setter method
     * @param context context
     * @param username username
     */
    public static void setUserName(Context context, String username) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME_KEY, username);
        editor.commit();
    }

    /**
     * Birthdate getter method
     * @param context context
     * @return birthdate
     */
    public static String getBirthDate(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(BIRTHDATE_KEY, BIRTHDATE_DEFAULT);
    }

    /**
     * Birthdate setter method
     * @param context context
     * @param birthdate birthdate
     */
    public static void setBirthDate(Context context, String birthdate) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(BIRTHDATE_KEY, birthdate);
        editor.commit();
    }



}