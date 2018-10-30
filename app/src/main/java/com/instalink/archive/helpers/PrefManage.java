package com.instalink.archive.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class PrefManage {

    int PRIVATE_MODE = 0;
    private String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    Context _context;
    SharedPreferences pref;
    String PREF_NAME = "instalink-login";
    SharedPreferences.Editor editor;

    public PrefManage(Context context) {
        this._context = context;
        pref = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(Boolean isFirstTimeToLaunch)
    {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH,isFirstTimeToLaunch);
        editor.commit();
    }

    public Boolean isFirstTimeToLaunch()
    {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH,true);
    }

}
