package com.localytics.android.sample.pushbroadcastreceiver;

import android.app.Application;

import com.localytics.android.Localytics;

public class MainApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        Localytics.setLoggingEnabled(true);
        Localytics.autoIntegrate(this);
    }
}
