package com.xanadu.marker;

import android.app.Application;
import android.content.Context;

import com.xanadu.marker.remote.BloggerApiUtil;

public class MyApp extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        BloggerApiUtil.initKey(getResources().getString(R.string.blogger_browser_key));
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }
}