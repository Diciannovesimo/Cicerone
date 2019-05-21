package com.nullpointerexception.cicerone.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.kinda.mtextfield.ExtendedEditText;
import com.kinda.mtextfield.TextFieldBoxes;
import com.nullpointerexception.cicerone.R;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

public class ItineraryActivity extends AppCompatActivity {

    private EditText mLuogo, mPuntoIncontro, mData, mOra, mMaxPart, mLingua, mCompenso;
    private ExtendedEditText mDescrizione;
    private FloatingActionButton newPoint;

    //Datepicker object
    Calendar calendar;
    private DatePickerDialog dpd;
    private String birthdayString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Nuovo itinerario");
        setSupportActionBar(toolbar);

        mLuogo = findViewById(R.id.place_et);
        mPuntoIncontro = findViewById(R.id.meetPlace_et);
        mData = findViewById(R.id.date_picker);
        mOra = findViewById(R.id.time_picker);
        mMaxPart = findViewById(R.id.MaxParticipants_et);
        mLingua = findViewById(R.id.language_et);
        mCompenso = findViewById(R.id.recompense_et);
        mDescrizione = findViewById(R.id.description_et);
        newPoint = findViewById(R.id.fab);

        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#FF5500"));

        MaterialSpinner spinner = findViewById(R.id.spinner_valute);
        spinner.setItems("€ Euro", "$ Dollaro", "£ Sterlina");
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {

            }
        });

        mData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                dpd = new DatePickerDialog(v.getContext(), R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        birthdayString = dayOfMonth + "/" + (month + 1) + "/" + year;
                        mData.setText(birthdayString);
                    }
                }, year, month, day);
                dpd.show();
            }
        });

        mOra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ItineraryActivity.this, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String text = "";
                        text += selectedHour;
                        text += ":";

                        if(selectedMinute == 0)
                            text += "00";
                        else
                            text += selectedMinute;

                        mOra.setText(text);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Scegli l'ora");
                mTimePicker.show();
            }
        });

        newPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog dialog;
                dialog = new AlertDialog.Builder(view.getContext()).setView(LayoutInflater.from(view.getContext()).inflate(R.layout.activity_dialog_tappe, null)).create();

                dialog.show();
            }
        });
    }
}
