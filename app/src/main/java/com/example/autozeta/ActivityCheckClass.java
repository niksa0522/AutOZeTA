package com.example.autozeta;

import android.app.Activity;

public class ActivityCheckClass {
    public static Activity mCurrentActivity = null;
    public static String otherUser=null;

    public static void SetActivity(Activity activity) { mCurrentActivity = activity; }

    public static void ClearActivity(Activity activity)
    {
        if(mCurrentActivity!=null)
        if (mCurrentActivity.equals(activity))
            mCurrentActivity = null;
    }

    public static Activity GetCurrentActivity() { return mCurrentActivity; }

    public static String getOtherUser(){return otherUser;}

    public static void setOtherUser(String otherUser) {
        ActivityCheckClass.otherUser = otherUser;
    }

    public static void clearOtherUser(String ou){
        if(otherUser!=null)
        if (otherUser.equals(ou))
            otherUser = null;
    }
}
