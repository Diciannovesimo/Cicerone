package com.nullpointerexception.cicerone.activities;

import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.kinda.alert.KAlertDialog;
import com.kinda.mtextfield.ExtendedEditText;
import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.components.AuthenticationManager;
import com.nullpointerexception.cicerone.components.BackEndInterface;
import com.nullpointerexception.cicerone.components.Itinerary;
import com.nullpointerexception.cicerone.components.Stage;
import com.nullpointerexception.cicerone.components.googleAutocompletationField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

public class ItineraryEditActivity extends AppCompatActivity {

    private static final String TAG = "ItineraryEditActivity";
    private EditText mLuogo, mPuntoIncontro, mData, mOra, mMaxPart, mLingua, mPlace, mPlaceDesc;
    private ExtendedEditText mDescrizione;
    private studio.carbonylgroup.textfieldboxes.ExtendedEditText mCompenso;
    private ImageView mAddStage, mRemoveStage;
    private TextView placeName_tv, placeDescription_tv, listStage_title, errorMsg_tv;
    private TextFieldBoxes luogo_box, punto_box, data_box, orario_box, partecipanti_box, place_box, lingua_box;
    private com.kinda.mtextfield.TextFieldBoxes descrizione_itinerario_box, descrizione_tappa_box;
    private Button create_stage;
    private LinearLayout linearLayout;
    private ScrollView scrollView;
    private String currency;
    private Stage stage;
    private HashMap<String, Stage> tappe = new HashMap<>();
    private googleAutocompletationField Google_field;

    private MaterialSpinner spinner;

    private int actual_field;

    //Istanza itinerario
    Itinerary itinerary;

    //Google things
    private List<Place.Field> fields;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

    //Datepicker object
    Calendar calendar;
    private DatePickerDialog dpd;
    private String birthdayString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Modifica itinerario");
        setSupportActionBar(toolbar);

        //Check if actionbar is initialized
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Change color of toolbar
        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#FF5500"));

        //Receiver data from intent
        Gson gson = new Gson();
        itinerary = gson.fromJson(getIntent().getStringExtra("test_itinerary"), Itinerary.class);

        //Initialize graphic interface
        initUI();

        //Initialize Google Places API
        Places.initialize(getApplicationContext(), getResources().getString(R.string.place_KEY));
        fields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        //Set text in fields
        setTextField();

        //Disable all EditText
        mLuogo.setEnabled(false);
        mPuntoIncontro.setEnabled(false);
        mData.setEnabled(false);
        mOra.setEnabled(false);

        mCompenso.setPrefix("€ ");
        currency = "€";
        spinner.setOnItemSelectedListener((MaterialSpinner.OnItemSelectedListener<String>) (view, position, id, item) -> {
            Log.i(TAG, item);
            switch(position) {
                case 0:
                    mCompenso.setPrefix("€ ");
                    currency = "€";
                    break;
                case 1:
                    mCompenso.setPrefix("$ ");
                    currency = "$";
                    break;
                case 2:
                    mCompenso.setPrefix("£ ");
                    currency = "£";
                    break;
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
            mTimePicker = new TimePickerDialog(ItineraryEditActivity.this, R.style.DialogTheme, (timePicker, selectedHour, selectedMinute) -> {
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
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(ItineraryEditActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.activity_dialog_tappe, null);

            descrizione_tappa_box = mView.findViewById(R.id.text_desc_boxes);
            mPlace = mView.findViewById(R.id.place_et);
            mPlaceDesc = mView.findViewById(R.id.placeDesc_et);
            place_box = mView.findViewById(R.id.place_box);
            create_stage = mView.findViewById(R.id.createStage_btn);

            mPlace.setEnabled(false);

            mBuilder.setView(mView);
            AlertDialog dialog = mBuilder.create();

            dialog.setOnDismissListener(dialog1 -> {
                if(tappe.size() == 0 && (listStage_title.getVisibility() == View.GONE) && (errorMsg_tv.getVisibility() == View.GONE))
                    listStage_title.setVisibility(View.VISIBLE);
            });

            place_box.setOnClickListener(v1 -> {
                actual_field = Google_field.PLACE.getN();
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(v1.getContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            });

            descrizione_tappa_box.setSimpleTextChangeWatcher((theNewText, isError) -> {
                if(theNewText.length() > 250)
                    descrizione_tappa_box.setError("Superato numero massimo caratteri", false);
            });

            create_stage.setOnClickListener(v12 -> {
                if(mPlaceDesc.getText().toString().length() > 250) {

                    descrizione_tappa_box.setError("Inserisci una descrizione più corta", false);

                }else if(mPlaceDesc.getText().toString().isEmpty()) {

                    descrizione_tappa_box.setError("Descrizione obligatoria", false);

                }else if(mPlace.getText().toString().isEmpty()) {

                    place_box.setError("Nome stage obligatoria", false);

                }else if(placeAlreadyExist(mPlace.getText().toString())){

                    runOnUiThread(() -> {
                        // Show error message
                        new KAlertDialog(mView.getContext(), KAlertDialog.ERROR_TYPE)
                                .setTitleText("Nome tappa già esistente")
                                .setContentText("È impossibile inserire due tappe uguali")
                                .setConfirmText("OK")
                                .show();
                    });

                }else{
                    if(errorMsg_tv.getVisibility() == View.VISIBLE)
                        errorMsg_tv.setVisibility(View.GONE);

                    listStage_title.setVisibility(View.GONE);

                    //Creo nuovo Layout
                    View newPlace = getLayoutInflater().inflate(R.layout.stage_layout, null);
                    placeDescription_tv = newPlace.findViewById(R.id.placeDescription_tv);
                    placeName_tv = newPlace.findViewById(R.id.placeName_tv);
                    mRemoveStage = newPlace.findViewById(R.id.mRemoveStage);

                    placeName_tv.setText(mPlace.getText().toString());
                    placeDescription_tv.setText(mPlaceDesc.getText().toString());

                    newPlace.setTag(stage.getName());

                    mRemoveStage.setOnClickListener(v13 -> {
                        tappe.remove(newPlace.getTag());
                        linearLayout.removeView(newPlace);

                        if(listStage_title.getVisibility() == View.GONE)
                            listStage_title.setVisibility(View.VISIBLE);
                    });

                    linearLayout.addView(newPlace, 0);

                    stage.setDescription(mPlaceDesc.getText().toString());
                    tappe.put(stage.getName(), stage);

                    dialog.dismiss();
                    scrollView.post(() -> ObjectAnimator.ofInt(scrollView, "scrollY",  scrollView.getBottom()).setDuration(800).start());
                }
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

                Place place = Autocomplete.getPlaceFromIntent(data);

                switch(actual_field) {
                    case 1:
                        mLuogo.setText(place.getName());
                        break;
                    case 2:
                        mPuntoIncontro.setText(place.getAddress());
                        break;
                    case 3:
                        stage = new Stage(place.getName(), place.getAddress(), place.getLatLng());

                        mPlace.setText(stage.getAddress());

                        if(!mPlaceDesc.getText().toString().isEmpty())
                            stage.setDescription(mPlaceDesc.getText().toString());

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
        mPlace = findViewById(R.id.place_et);
        luogo_box = findViewById(R.id.luogo_box);
        punto_box = findViewById(R.id.punto_box);
        data_box = findViewById(R.id.data_box);
        orario_box = findViewById(R.id.orario_box);
        partecipanti_box = findViewById(R.id.partecipanti_box);
        descrizione_itinerario_box = findViewById(R.id.descrizione_box);
        lingua_box = findViewById(R.id.lingua_box);
        linearLayout = findViewById(R.id.listStages);
        scrollView = findViewById(R.id.scrollView);
        listStage_title = findViewById(R.id.listStage_title);
        errorMsg_tv = findViewById(R.id.errorMsg_tv);
    }

    private boolean placeAlreadyExist(String field) {
        for (Map.Entry<String, Stage> entry : tappe.entrySet()) {
            if(field.equals(entry.getValue().getAddress()))
                return true;
        }
        return false;
    }

    //Impostazioni menù
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_itinerary, menu);

        return true;
    }

    private boolean checkField() {
        boolean alright = true;

        if(mLuogo.getText().toString().isEmpty()) {
            luogo_box.setError("Il campo non può essere vuoto", false);
            alright = false;
        }

        if(mPuntoIncontro.getText().toString().isEmpty()) {
            punto_box.setError("Il campo non può essere vuoto", false);
            alright = false;
        }

        if(mData.getText().toString().isEmpty()) {
            data_box.setError("Il campo non può essere vuoto", false);
            alright = false;
        }

        if(mOra.getText().toString().isEmpty()) {
            orario_box.setError("Il campo non può essere vuoto", false);
            alright = false;
        }

        if(mMaxPart.getText().toString().isEmpty()) {
            partecipanti_box.setError("Il campo non può essere vuoto", false);
            alright = false;
        }

        if(mLingua.getText().toString().isEmpty()) {
            lingua_box.setError("Il campo non può essere vuoto", false);
            alright = false;
        }

        if(mDescrizione.getText().toString().isEmpty()) {
            descrizione_itinerario_box.setError("Il campo non può essere vuoto", false);
            alright = false;
        }

        if(tappe.size() == 0) {
            listStage_title.setVisibility(View.GONE);
            errorMsg_tv.setVisibility(View.VISIBLE);
            alright = false;
        }

        return alright;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.createItinerary) {

            itinerary = new Itinerary();

            if(checkField()) {
                //Inserimento specifiche nell'itinerario
                itinerary.setLocation(mLuogo.getText().toString());
                itinerary.setMeetingPlace(mPuntoIncontro.getText().toString());
                itinerary.setDate(mData.getText().toString());
                itinerary.setMeetingTime(mOra.getText().toString());
                itinerary.setMaxParticipants(Integer.parseInt(mMaxPart.getText().toString()));
                itinerary.setLanguage(mLingua.getText().toString().trim());
                itinerary.setCurrency(currency);
                itinerary.setIdCicerone(AuthenticationManager.get().getUserLogged().getId());
                itinerary.generateId();

                if(!mCompenso.getText().toString().equals(""))
                    itinerary.setPrice(Float.parseFloat(mCompenso.getText().toString()));

                itinerary.setDescription(mDescrizione.getText().toString());

                //Creao lista adatta per il passaggio dei dati alla BackEndInterface
                List<Stage> placeInterface = new ArrayList<>();

                for (Map.Entry<String, Stage> entry : tappe.entrySet())
                    placeInterface.add(entry.getValue());

                itinerary.setStages(placeInterface);

                BackEndInterface.get().storeEntity(itinerary, new BackEndInterface.OnOperationCompleteListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), "Itinerario creato", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(getApplicationContext(), "Errore nel caricamento dell'itinerario\nRiprova", Toast.LENGTH_SHORT).show();

                    }
                });

            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setTextField() {
        mLuogo.setText(itinerary.getLocation());
        mPuntoIncontro.setText(itinerary.getMeetingPlace());
        mData.setText(itinerary.getDate());
        mOra.setText(itinerary.getMeetingTime());
        mLingua.setText(itinerary.getLanguage());
        mMaxPart.setText(String.valueOf(itinerary.getMaxParticipants()));
        mCompenso.setText(String.valueOf(itinerary.getPrice()));
        mCompenso.setPrefix(itinerary.getCurrency() + " ");

        //Initialize spinner
        spinner = findViewById(R.id.spinner_valute);
        spinner.setItems("€ Euro", "$ Dollaro", "£ Sterlina");
        switch (itinerary.getCurrency()) {
            case "€":
                spinner.setSelectedIndex(0);
                break;
            case "$":
                spinner.setSelectedIndex(1);
                break;
            case "£":
                spinner.setSelectedIndex(2);
                break;
        }

        mDescrizione.setText(itinerary.getDescription());

        for(int i = 0; i < itinerary.getStages().size(); ++i)
            tappe.put(itinerary.getStages().get(i).getName(), itinerary.getStages().get(i));

        //Creo nuovi Layout tappe
        if(tappe.size() > 0) {
            listStage_title.setVisibility(View.INVISIBLE);

            for (Map.Entry<String, Stage> entry : tappe.entrySet()) {
                View newPlace = getLayoutInflater().inflate(R.layout.stage_layout, null);
                placeDescription_tv = newPlace.findViewById(R.id.placeDescription_tv);
                placeName_tv = newPlace.findViewById(R.id.placeName_tv);
                mRemoveStage = newPlace.findViewById(R.id.mRemoveStage);

                placeName_tv.setText(entry.getValue().getName());
                placeDescription_tv.setText(entry.getValue().getDescription());

                newPlace.setTag(entry.getValue().getName());

                mRemoveStage.setOnClickListener(v13 -> {
                    tappe.remove(newPlace.getTag());
                    linearLayout.removeView(newPlace);
                });

                linearLayout.addView(newPlace, 0);
            }
        }else {
            listStage_title.setVisibility(View.INVISIBLE);
        }
    }

}
