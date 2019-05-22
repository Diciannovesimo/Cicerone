package com.nullpointerexception.cicerone.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.kinda.mtextfield.ExtendedEditText;
import com.kinda.mtextfield.TextFieldBoxes;
import com.nullpointerexception.cicerone.R;

public class DialogTappe extends AppCompatActivity {

    private Button mCreateStage;
    private studio.carbonylgroup.textfieldboxes.ExtendedEditText mPlace;
    private ExtendedEditText mDescription;
    private TextFieldBoxes desc_box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_tappe);

        mPlace = findViewById(R.id.place_et);
        mCreateStage = findViewById(R.id.createStage_btn);
        desc_box = findViewById(R.id.text_desc_boxes);
    }
}
