package com.nullpointerexception.cicerone.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.FirebaseApp;
import com.nullpointerexception.cicerone.components.LogManager;

public class SplashScreen extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        LogManager.get().initialize(getApplicationContext());

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
