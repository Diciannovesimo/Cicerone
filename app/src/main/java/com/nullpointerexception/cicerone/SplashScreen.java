package com.nullpointerexception.cicerone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

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
