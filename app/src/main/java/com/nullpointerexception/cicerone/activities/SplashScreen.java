package com.nullpointerexception.cicerone.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.FirebaseApp;
import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.components.LogManager;

public class SplashScreen extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        FacebookSdk.setApplicationId(getResources().getString(R.string.facebook_app_id));
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        LogManager.get().initialize(this);

        new Handler().post(new Runnable()
        {
            @Override
            public void run()
            {
                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                finish();
            }
        });
    }
}
