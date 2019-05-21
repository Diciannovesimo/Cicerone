package com.nullpointerexception.cicerone.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.nullpointerexception.cicerone.R;

public class DialogTappe extends AppCompatActivity {

    private Button mCreateStage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_tappe);

        mCreateStage = findViewById(R.id.createStage_btn);


    }
}
