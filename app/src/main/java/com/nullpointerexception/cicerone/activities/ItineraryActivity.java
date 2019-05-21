package com.nullpointerexception.cicerone.activities;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.kinda.mtextfield.ExtendedEditText;
import com.nullpointerexception.cicerone.R;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

//Google SDk
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

public class ItineraryActivity extends AppCompatActivity {

    private EditText mLuogo, mPuntoIncontro, mData, mOra, mMaxPart, mLingua, mCompenso, mMaps;
    private ExtendedEditText mDescrizione;
    private ImageView mAddStage;
    private TextFieldBoxes luogo_box;
    private int AUTOCOMPLETE_REQUEST_CODE = 1;

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

        // Initialize Places.
        Places.initialize(getApplicationContext(), getResources().getString(R.string.place_KEY));

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

        mLuogo = findViewById(R.id.place_et);
        mPuntoIncontro = findViewById(R.id.meetPlace_et);
        mData = findViewById(R.id.date_picker);
        mOra = findViewById(R.id.time_picker);
        mMaxPart = findViewById(R.id.MaxParticipants_et);
        mLingua = findViewById(R.id.language_et);
        mCompenso = findViewById(R.id.recompense_et);
        mDescrizione = findViewById(R.id.description_et);
        mAddStage = findViewById(R.id.addStage_btn);
        luogo_box = findViewById(R.id.luogo_box);

        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#FF5500"));

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

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

        mAddStage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog dialog;
                dialog = new AlertDialog.Builder(view.getContext()).setView(LayoutInflater.from(view.getContext()).inflate(R.layout.activity_dialog_tappe, null)).create();

                dialog.show();
            }
        });

        luogo_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(v.getContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("it_view", "Place: " + place.getName() + ", " + place.getId());
                if(!mLuogo.getText().toString().isEmpty())
                    mLuogo.setText("");
                else
                    mLuogo.setText(place.getName());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("it_view", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
}
