package com.nullpointerexception.cicerone.activities;

import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.kinda.mtextfield.ExtendedEditText;
import com.nullpointerexception.cicerone.R;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

//Google SDk
import com.google.android.libraries.places.api.Places;


import com.nullpointerexception.cicerone.components.googleAutocompletationField;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

public class ItineraryActivity extends AppCompatActivity {

    private static final String TAG = "ItineraryActivity";
    private EditText mLuogo, mPuntoIncontro, mData, mOra, mMaxPart, mLingua, mMaps, mPlace, mPlaceDesc;
    private ExtendedEditText mDescrizione;
    private studio.carbonylgroup.textfieldboxes.ExtendedEditText mCompenso;
    private ImageView mAddStage;
    private TextView placeName_tv, placeDescription_tv;
    private TextFieldBoxes luogo_box, punto_box, data_box, orario_box, partecipanti_box, place_box;
    private com.kinda.mtextfield.TextFieldBoxes descrizione_itinerario_box, descrizione_tappa_box;
    private Button create_stage;
    private String place_string[] = new String[2];
    private List<Place.Field> fields;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private googleAutocompletationField Google_field;
    private LinearLayout linearLayout;
    private ScrollView scrollView;
    private int actual_field;

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

        //Initialize graphic interface
        initUI();

        //Disable all EditText
        mLuogo.setEnabled(false);
        mPuntoIncontro.setEnabled(false);
        mData.setEnabled(false);
        mOra.setEnabled(false);

        //Change color of toolbar
        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#FF5500"));

        //Check if actionbar is initialized
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initialize Google Places API
        Places.initialize(getApplicationContext(), getResources().getString(R.string.place_KEY));
        fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

        //Initialize spinner
        MaterialSpinner spinner = findViewById(R.id.spinner_valute);
        spinner.setItems("€ Euro", "$ Dollaro", "£ Sterlina");
        mCompenso.setPrefix("€ ");
        spinner.setOnItemSelectedListener((MaterialSpinner.OnItemSelectedListener<String>) (view, position, id, item) -> {
            Log.i(TAG, item);
            switch(position) {
                case 0:
                    mCompenso.setPrefix("€ ");break;
                case 1:
                    mCompenso.setPrefix("$ ");break;
                case 2:
                    mCompenso.setPrefix("£ ");break;
            }
        });

        //Listener
        data_box.setOnClickListener(v -> {
            calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            dpd = new DatePickerDialog(v.getContext(), R.style.DialogTheme, (view, year12, month12, dayOfMonth) -> {
                birthdayString = dayOfMonth + "/" + (month12 + 1) + "/" + year12;
                mData.setText(birthdayString);
            }, year, month, day);
            dpd.show();
        });

        orario_box.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(ItineraryActivity.this, R.style.DialogTheme, (timePicker, selectedHour, selectedMinute) -> {
                String text = "";
                text += selectedHour;
                text += ":";

                if(selectedMinute == 0)
                    text += "00";
                else
                    text += selectedMinute;

                mOra.setText(text);
            }, hour, minute, true);
            mTimePicker.setTitle("Scegli l'ora");
            mTimePicker.show();
        });

        mAddStage.setOnClickListener(v -> {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(ItineraryActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.activity_dialog_tappe, null);

            descrizione_tappa_box = mView.findViewById(R.id.text_desc_boxes);
            mPlace = mView.findViewById(R.id.place_et);
            mPlaceDesc = mView.findViewById(R.id.placeDesc_et);
            place_box = mView.findViewById(R.id.place_box);
            create_stage = mView.findViewById(R.id.createStage_btn);

            mBuilder.setView(mView);
            AlertDialog dialog = mBuilder.create();

            place_box.setOnClickListener(v1 -> {
                actual_field = Google_field.PLACE.getN();
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(v1.getContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            });

            descrizione_tappa_box.setSimpleTextChangeWatcher((theNewText, isError) -> {
                if(theNewText.length() > 250) {
                    descrizione_tappa_box.setError("Superato numero massimo caratteri", true);
                }
            });

            create_stage.setOnClickListener(v12 -> {
                View newPlace = getLayoutInflater().inflate(R.layout.stage_layout, null);

                placeDescription_tv = newPlace.findViewById(R.id.placeDescription_tv);
                placeName_tv = newPlace.findViewById(R.id.placeName_tv);

                placeName_tv.setText(mPlace.getText().toString());
                placeDescription_tv.setText(mPlaceDesc.getText().toString());

                linearLayout.addView(newPlace, 0);
                dialog.dismiss();

                scrollView.post(() -> ObjectAnimator.ofInt(scrollView, "scrollY",  scrollView.getBottom()).setDuration(800).start());
            });


            dialog.show();
        });

        luogo_box.setOnClickListener(v -> {
            actual_field = Google_field.LUOGO.getN();
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(v.getContext());
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });

        punto_box.setOnClickListener(v -> {
            actual_field = Google_field.PUNTO_DI_INCONTRO.getN();
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(v.getContext());
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            mPuntoIncontro.setText(place_string[1]);
        });

        partecipanti_box.setSimpleTextChangeWatcher((theNewText, isError) -> {
            Integer n_part = 0;

            if(!theNewText.equals(""))
                n_part = Integer.parseInt(theNewText);

            if(n_part > 100) {
                partecipanti_box.setError("Il numero massimo di partecipanti è 100", true);
            }
        });

        descrizione_itinerario_box.setSimpleTextChangeWatcher((theNewText, isError) -> {
            if(theNewText.length() > 250) {
                descrizione_itinerario_box.setError("Superato numero massimo caratteri", true);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //Log.i("it_view", "Place: " + place.getName() + ", " + place.getId());
                Place place = Autocomplete.getPlaceFromIntent(data);
                place_string[0] = place.getName();
                place_string[1] = String.valueOf(place.getLatLng());

                switch(actual_field) {
                    case 1:
                        mLuogo.setText(place_string[0]);
                        break;
                    case 2:
                        mPuntoIncontro.setText(place_string[0]);
                        break;
                    case 3:
                        if(mPlace != null)
                            mPlace.setText(place_string[0]);
                        break;
                }
            }
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Log.i("it_view", status.getStatusMessage());
        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.
        }
    }

    private void initUI() {
        mLuogo = findViewById(R.id.luogo_et);
        mPuntoIncontro = findViewById(R.id.meetPlace_et);
        mMaxPart = findViewById(R.id.MaxParticipants_et);
        mLingua = findViewById(R.id.language_et);
        mCompenso = findViewById(R.id.recompense_et);
        mDescrizione = findViewById(R.id.description_et);
        mAddStage = findViewById(R.id.addStage_btn);
        mData = findViewById(R.id.date_picker);
        mOra = findViewById(R.id.time_picker);
        luogo_box = findViewById(R.id.luogo_box);
        punto_box = findViewById(R.id.punto_box);
        data_box = findViewById(R.id.data_box);
        orario_box = findViewById(R.id.orario_box);
        partecipanti_box = findViewById(R.id.partecipanti_box);
        descrizione_itinerario_box = findViewById(R.id.descrizione_box);
        linearLayout = findViewById(R.id.listStages);
        scrollView = findViewById(R.id.scrollView);
    }
}

