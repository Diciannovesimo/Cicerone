package com.nullpointerexception.cicerone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.components.AuthenticationManager;

public class SplashScreen extends AppCompatActivity
{

    private boolean stopHandler = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        FacebookSdk.setApplicationId(getResources().getString(R.string.facebook_app_id));
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        AuthenticationManager.LoginAttempt loginAttempt = AuthenticationManager.get().initialize(this);

        if(loginAttempt != null)
        {
            loginAttempt.addOnLoginResultListener(result ->
            {
                stopHandler = true;

                if(result)
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                else
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));

                finish();
            });
        }
        else
        {
            stopHandler = true;
            startActivity(new Intent(SplashScreen.this, LoginActivity.class));
            finish();
        }

        new Handler().postDelayed(() ->
        {
            if( ! stopHandler)
            {
                if(AuthenticationManager.get().isUserLogged())
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                else
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));

                finish();
            }
        }, 10000);
    }
}