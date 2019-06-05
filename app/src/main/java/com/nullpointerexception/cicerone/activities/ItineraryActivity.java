package com.nullpointerexception.cicerone.activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.kinda.alert.KAlertDialog;
import com.kinda.mtextfield.ExtendedEditText;
import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.components.AuthenticationManager;
import com.nullpointerexception.cicerone.components.BackEndInterface;
import com.nullpointerexception.cicerone.components.Itinerary;
import com.nullpointerexception.cicerone.components.ObjectSharer;
import com.nullpointerexception.cicerone.components.Stage;
import com.nullpointerexception.cicerone.components.googleAutocompletationField;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 *      ItineraryActivity
 *
 *      Activity where user can create a new Itinerary.
 *
 *      @author Claudio
 */
public class ItineraryActivity extends AppCompatActivity {

    private static final String TAG = "ItineraryActivity";
    private EditText mLuogo, mPuntoIncontro, mData, mOra, mMaxPart, mLingua, mPlace, mPlaceDesc;
    private ExtendedEditText mDescrizione;
    private studio.carbonylgroup.textfieldboxes.ExtendedEditText mCompenso;
    private ImageView mAddStage, mRemoveStage, findPosition;
    private TextView placeName_tv, placeAddress_tv, placeDescription_tv, listStage_title, errorMsg_tv;
    private TextFieldBoxes luogo_box, punto_box, data_box, orario_box, partecipanti_box, place_box, lingua_box;
    private com.kinda.mtextfield.TextFieldBoxes descrizione_itinerario_box, descrizione_tappa_box;
    private Button create_stage;
    private List<Place.Field> fields;
    private Stage stage;
    private HashMap<String, Stage> tappe = new HashMap<>();
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private googleAutocompletationField Google_field;
    private LinearLayout linearLayout;
    private ScrollView scrollView;
    private String currency;
    private Toolbar toolbar;
    private MaterialSpinner spinner;
    private Menu menu;

    //Istanza itinerario
    private Itinerary itinerary;
    private int actual_field;
    private boolean edit_mode = false;

    //Datepicker object
    Calendar calendar;
    private DatePickerDialog dpd;
    private String birthdayString;

    //GPS permission
    private boolean gpsPermission = false;

    private PlaceLikelihood MaxPlaceLikelihood;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);

        //Initialize graphic interface
        initUI();

        //Receiver data from intent if the user wants to edit an itinerary
        if(ObjectSharer.get().getSharedObject("edit_itinerary") != null)
        {
            edit_mode = true;
            itinerary = (Itinerary)ObjectSharer.get().getSharedObject("edit_itinerary");
            setTextField(itinerary);
        }

        if(edit_mode)
            toolbar.setTitle(getResources().getString(R.string.ItinerayEditActivity_Title));
        else
            toolbar.setTitle(getResources().getString(R.string.ItinerayActivity_Title));

        //Set toolbar
        setSupportActionBar(toolbar);

        //Change color of toolbar
        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#FF5500"));

        //Check if actionbar is initialized
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initialize Google Places API
        Places.initialize(getApplicationContext(), getResources().getString(R.string.place_KEY));
        fields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        PlacesClient placesClient = Places.createClient(this);

        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.builder(fields).build();

        //Initialize spinner
        spinner = findViewById(R.id.spinner_valute);
        spinner.setItems("€ Euro", "$ Dollaro", "£ Sterlina");
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

        //Listeners
        /**
         * Listener for Data field
         *
         */
        data_box.setOnClickListener(v -> {
            calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            dpd = new DatePickerDialog(v.getContext(), R.style.DialogTheme, (view, year12, month12, dayOfMonth) -> {
                if(dayOfMonth < 10)
                    birthdayString += "0" + dayOfMonth + "/";

                birthdayString = dayOfMonth + "/";
                if((month12 + 1) < 10)
                    birthdayString += "0" + (month12 + 1) + "/" + year12;
                else
                    birthdayString += (month12 + 1) + "/" + year12;
                mData.setText(birthdayString);
            }, year, month, day);
            dpd.show();
        });

        /**
         * Listener for time field
         */
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
                else if(selectedMinute < 10) {
                    text += "0";
                    text += selectedMinute;
                } else{
                    text += selectedMinute;
                }

                mOra.setText(text);
            }, hour, minute, true);
            mTimePicker.setTitle(getResources().getString(R.string.time_picker));
            mTimePicker.show();
        });

        /**
         * Listener to add a place in a itinerary
         */
        mAddStage.setOnClickListener(v -> {

            //New layout for place's dialog
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(ItineraryActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.activity_dialog_tappe, null);

            //Init field of place's layout
            descrizione_tappa_box = mView.findViewById(R.id.text_desc_boxes);
            mPlace = mView.findViewById(R.id.place_et);
            mPlaceDesc = mView.findViewById(R.id.placeDesc_et);
            place_box = mView.findViewById(R.id.place_box);
            create_stage = mView.findViewById(R.id.createStage_btn);
            findPosition = mView.findViewById(R.id.findPosition);

            mPlace.setEnabled(false);

            //Create a new dialog
            mBuilder.setView(mView);
            AlertDialog dialog = mBuilder.create();

            //If dialog is dismissed, shows the listStage_title
            dialog.setOnDismissListener(dialog1 -> {
                if(tappe.size() == 0 && (listStage_title.getVisibility() == View.GONE) && (errorMsg_tv.getVisibility() == View.GONE))
                    listStage_title.setVisibility(View.VISIBLE);
            });

            //Listener to open Google Place autocomplete intent
            place_box.setOnClickListener(v1 -> {
                actual_field = Google_field.PLACE.getN();
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(v1.getContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            });

            //Listener to capture user's position
            findPosition.setOnClickListener(v14 -> {

                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                gpsPermission = checkLocationPermission();

                if(gpsPermission) {

                    if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
                        placeResponse.addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FindCurrentPlaceResponse response = task.getResult();
                                MaxPlaceLikelihood = null;
                                int counter = 0;

                                for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                                    if(counter == 0)
                                        MaxPlaceLikelihood = placeLikelihood;

                                    counter++;

                                    if(MaxPlaceLikelihood.getLikelihood() <= placeLikelihood.getLikelihood())
                                        MaxPlaceLikelihood = placeLikelihood;

                                    Log.i(TAG, String.format("Place '%s' has likelihood: %f",
                                            placeLikelihood.getPlace().getName(),
                                            placeLikelihood.getLikelihood()));
                                }
                                //Set the name of Stage in EditText
                                mPlace.setText(MaxPlaceLikelihood.getPlace().getName());
                                Log.i(TAG, MaxPlaceLikelihood.getPlace().getName());
                                //Set attributes of Place in stage
                                stage = new Stage();
                                stage.setName(MaxPlaceLikelihood.getPlace().getName());
                                stage.setCoordinates(MaxPlaceLikelihood.getPlace().getLatLng());
                                Log.i(TAG, MaxPlaceLikelihood.getPlace().getLatLng().toString());
                                stage.setAddress(MaxPlaceLikelihood.getPlace().getAddress());
                                Log.i(TAG, MaxPlaceLikelihood.getPlace().getAddress());
                            } else {
                                Exception exception = task.getException();
                                if (exception instanceof ApiException) {
                                    ApiException apiException = (ApiException) exception;
                                    Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                                }
                            }
                        });
                    }
                }else {
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }

            });

            //Check if the number of character in description field is more than 250 character
            descrizione_tappa_box.setSimpleTextChangeWatcher((theNewText, isError) -> {
                if(theNewText.length() > 250)
                    descrizione_tappa_box.setError(getResources().getString(R.string.max_char), false);
            });

            //Listener to create a new place for a itinerary
            create_stage.setOnClickListener(v12 -> {
                //Check if the number of character in description field is more than 250 character
                if(mPlaceDesc.getText().toString().length() > 250) {

                    descrizione_tappa_box.setError(getResources().getString(R.string.insert_shorter_desc), false);

                    //Check if the description is empty
                }else if(mPlaceDesc.getText().toString().isEmpty()) {

                    descrizione_tappa_box.setError(getResources().getString(R.string.no_desc), false);

                    //Check if the name's field is empty
                }else if(mPlace.getText().toString().isEmpty()) {

                    place_box.setError(getResources().getString(R.string.no_place_name), false);

                    //Check if the place already exist
                }else if(placeAlreadyExist(stage.getCoordinates())){

                    runOnUiThread(() -> {
                        // Show error message
                        new KAlertDialog(mView.getContext(), KAlertDialog.ERROR_TYPE)
                                .setTitleText(getResources().getString(R.string.error_dialog_title))
                                .setContentText(getResources().getString(R.string.error_dialog_content))
                                .setConfirmText(getResources().getString(R.string.error_dialog_confirmText))
                                .show();
                    });

                    //It proceeds with the creation of the place both graphically and by adding it
                    // to the hashmap.
                }else{
                    if(errorMsg_tv.getVisibility() == View.VISIBLE)
                        errorMsg_tv.setVisibility(View.GONE);

                    listStage_title.setVisibility(View.GONE);

                    //Creo nuovo Layout
                    View newPlace = getLayoutInflater().inflate(R.layout.stage_layout, null);
                    placeDescription_tv = newPlace.findViewById(R.id.placeDescription_tv);
                    placeName_tv = newPlace.findViewById(R.id.placeName_tv);
                    placeAddress_tv = newPlace.findViewById(R.id.placeAddress_tv);
                    mRemoveStage = newPlace.findViewById(R.id.mRemoveStage);

                    placeName_tv.setText(mPlace.getText().toString());
                    placeDescription_tv.setText(mPlaceDesc.getText().toString());
                    placeAddress_tv.setText(stage.getAddress());

                    //Add a tag to the view to allow the tracking of the view
                    newPlace.setTag(stage.getName());

                    //Listener to remove a place
                    mRemoveStage.setOnClickListener(v13 -> {
                        tappe.remove(newPlace.getTag());
                        linearLayout.removeView(newPlace);

                        if(listStage_title.getVisibility() == View.GONE && tappe.size() == 0)
                            listStage_title.setVisibility(View.VISIBLE);
                    });

                    linearLayout.addView(newPlace, 0);

                    stage.setDescription(mPlaceDesc.getText().toString());

                    //Add a place to place list
                    tappe.put(stage.getName(), stage);

                    dialog.dismiss();

                    //Scroll to the end of the scrollView
                    scrollView.post(() -> ObjectAnimator.ofInt(scrollView, "scrollY",  scrollView.getBottom()).setDuration(800).start());
                }
            });

            dialog.show();
        });

        /**
         * Listener for location's field
         */
        luogo_box.setOnClickListener(v -> {
            actual_field = Google_field.LUOGO.getN();
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(v.getContext());
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });

        /**
         * Listener for meeting place's field
         */
        punto_box.setOnClickListener(v -> {
            actual_field = Google_field.PUNTO_DI_INCONTRO.getN();
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(v.getContext());
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });

        /**
         * Check if the number of max. participants has been exceeded
         */
        partecipanti_box.setSimpleTextChangeWatcher((theNewText, isError) -> {
            Integer n_part = 0;

            if(!theNewText.equals(""))
                n_part = Integer.parseInt(theNewText);

            if(n_part > 100) {
                partecipanti_box.setError(getResources().getString(R.string.max_char), true);
            }
        });

        /**
         * Check if the number of character in description field is more than 250 character
         */
        descrizione_itinerario_box.setSimpleTextChangeWatcher((theNewText, isError) -> {
            if(theNewText.length() > 250) {
                descrizione_itinerario_box.setError(getResources().getString(R.string.max_char), true);
            }
        });

    }

    /**
     * Used to receive data from Google Place autocomplete intent.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller
     */
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
                        mPuntoIncontro.setText(place.getName());
                        break;
                    case 3:
                        stage = new Stage(place.getName(), place.getAddress(), place.getLatLng());

                        mPlace.setText(stage.getName());

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

    /**
     * initialize the ui
     */
    private void initUI() {
        toolbar = findViewById(R.id.toolbar);
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

    /**
     * @param menu The options menu in which you place your items
     * @return true if menu has been create
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_itinerary, menu);

        this.menu = menu;
        setItemTitle();
        return true;
    }

    /**
     * If the menu's button has benn pressed, the itinerary will be loaded on DB.
     *
     * @param item The menu item that was selected
     * @return Return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.createItinerary) {

            if (checkField()) {

                if (tappe.size() > 0) {

                    if (!edit_mode) {
                        //New itinerary, ready to be loaded on Firebase DB
                        Itinerary new_itinerary = new Itinerary();

                        //Inserimento specifiche nell'itinerario
                        new_itinerary.setLocation(mLuogo.getText().toString());
                        new_itinerary.setMeetingPlace(mPuntoIncontro.getText().toString());

                        //Formatting date
                        try {
                            Date day = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY).parse(mData.getText().toString());
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd", Locale.ITALY);
                            String dayString = formatter.format(day);
                            new_itinerary.setDate(dayString);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }

                        try {
                            Date time = new SimpleDateFormat("HH:mm", Locale.ITALY).parse(mOra.getText().toString());
                            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.ITALY);
                            String timeString = formatter.format(time);
                            new_itinerary.setMeetingTime(timeString);
                        }catch (Exception e){
                            Log.e(TAG, e.toString());
                        }

                        if (!mMaxPart.getText().toString().isEmpty())
                            new_itinerary.setMaxParticipants(Integer.parseInt(mMaxPart.getText().toString()));

                        new_itinerary.setLanguage(mLingua.getText().toString().trim());
                        new_itinerary.setCurrency(currency);
                        new_itinerary.setIdCicerone(AuthenticationManager.get().getUserLogged().getId());
                        new_itinerary.generateId();

                        if (!mCompenso.getText().toString().equals(""))
                            new_itinerary.setPrice(Float.parseFloat(mCompenso.getText().toString()));

                        new_itinerary.setDescription(mDescrizione.getText().toString());

                        //Creao lista adatta per il passaggio dei dati alla BackEndInterface
                        List<Stage> placeInterface = new ArrayList<>();

                        for (Map.Entry<String, Stage> entry : tappe.entrySet())
                            placeInterface.add(entry.getValue());

                        new_itinerary.setStages(placeInterface);

                        final Itinerary checkItinerary = new Itinerary();
                        checkItinerary.setId(new_itinerary.getId());

                        //TODO: Vedere con Luca questo metodo
                        //Check if the itinerary already exist
                        BackEndInterface.get().getEntity(checkItinerary, new BackEndInterface.OnOperationCompleteListener() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(getApplicationContext(), "L'itinerario esiste già", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError() {
                                //Load the itinerary on Firebase DB
                                BackEndInterface.get().storeEntity(new_itinerary, new BackEndInterface.OnOperationCompleteListener() {
                                    @Override
                                    public void onSuccess() {

                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.succes_create_itinerary_toast), Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

                                    @Override
                                    public void onError() {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.failure_edit_toast), Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        });

                        /*
                        //Load the itinerary on Firebase DB
                                BackEndInterface.get().storeEntity(new_itinerary, new BackEndInterface.OnOperationCompleteListener() {
                                    @Override
                                    public void onSuccess() {

                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.succes_create_itinerary_toast), Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

                                    @Override
                                    public void onError() {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.failure_edit_toast), Toast.LENGTH_SHORT).show();

                                    }
                                });
                         */

                        return true;
                    } else {
                        BackEndInterface.get().removeEntity(itinerary, new BackEndInterface.OnOperationCompleteListener() {
                            @Override
                            public void onSuccess() {
                                Itinerary new_itinerary = new Itinerary();

                                //Inserimento specifiche nell'itinerario
                                new_itinerary.setLocation(mLuogo.getText().toString());
                                new_itinerary.setMeetingPlace(mPuntoIncontro.getText().toString());

                                //Formatting date
                                try {
                                    Date day = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY).parse(mData.getText().toString());
                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd", Locale.ITALY);
                                    String dayString = formatter.format(day);
                                    new_itinerary.setDate(dayString);
                                } catch (Exception e) {
                                    Log.e(TAG, e.toString());
                                }

                                try {
                                    Date time = new SimpleDateFormat("HH:mm", Locale.ITALY).parse(mOra.getText().toString());
                                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.ITALY);
                                    String timeString = formatter.format(time);
                                    new_itinerary.setMeetingTime(timeString);
                                }catch (Exception e){
                                    Log.e(TAG, e.toString());
                                }

                                if (!mMaxPart.getText().toString().isEmpty())
                                    new_itinerary.setMaxParticipants(Integer.parseInt(mMaxPart.getText().toString()));

                                new_itinerary.setLanguage(mLingua.getText().toString().trim());
                                new_itinerary.setCurrency(currency);
                                new_itinerary.setIdCicerone(AuthenticationManager.get().getUserLogged().getId());
                                new_itinerary.generateId();

                                if (!mCompenso.getText().toString().equals(""))
                                    new_itinerary.setPrice(Float.parseFloat(mCompenso.getText().toString()));

                                new_itinerary.setDescription(mDescrizione.getText().toString());

                                //Creao lista adatta per il passaggio dei dati alla BackEndInterface
                                List<Stage> placeInterface = new ArrayList<>();

                                for (Map.Entry<String, Stage> entry : tappe.entrySet())
                                    placeInterface.add(entry.getValue());

                                new_itinerary.setStages(placeInterface);

                                BackEndInterface.get().storeEntity(new_itinerary, new BackEndInterface.OnOperationCompleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        ObjectSharer.get().shareObject("new_itinerary", new_itinerary);

                                        //Delete received itinerary
                                        ObjectSharer.get().remove("edit_itinerary");

                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.succes_edit_toast), Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

                                    @Override
                                    public void onError() {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.failure_edit_toast), Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }

                            @Override
                            public void onError() {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    return true;
                } else if (id == R.id.createItinerary) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_places), Toast.LENGTH_SHORT).show();
                    errorMsg_tv.setVisibility(View.VISIBLE);
                    listStage_title.setVisibility(View.GONE);
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Reads data from itinerary and sets them in the right EditText
     */
    private void setTextField(Itinerary itinerary) {
        mLuogo.setText(itinerary.getLocation());
        mPuntoIncontro.setText(itinerary.getMeetingPlace());

        //Change date format
        try {
            Date day = new SimpleDateFormat("yyyy/MM/dd", Locale.ITALY).parse(itinerary.getDate());
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);
            String dayString = formatter.format(day);
            mData.setText(dayString);
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        mOra.setText(itinerary.getMeetingTime());
        mLingua.setText(itinerary.getLanguage());
        mMaxPart.setText(String.valueOf(itinerary.getMaxParticipants()));
        mCompenso.setText(String.valueOf(itinerary.getPrice()));
        mCompenso.setPrefix(itinerary.getCurrency() + " ");

        //Initialize spinner
        spinner = findViewById(R.id.spinner_valute);
        spinner.setItems("€ Euro", "$ Dollaro", "£ Sterlina");
        if(itinerary.getCurrency() != null)
            switch (itinerary.getCurrency())
            {
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

        if(itinerary.getStages() != null)
        for(int i = 0; i < itinerary.getStages().size(); ++i)
            tappe.put(itinerary.getStages().get(i).getName(), itinerary.getStages().get(i));

        //Creo nuovi Layout tappe
        if(tappe.size() > 0)
        {
            listStage_title.setVisibility(View.GONE);

            for (Map.Entry<String, Stage> entry : tappe.entrySet()) {
                View newPlace = getLayoutInflater().inflate(R.layout.stage_layout, null);
                placeDescription_tv = newPlace.findViewById(R.id.placeDescription_tv);
                placeName_tv = newPlace.findViewById(R.id.placeName_tv);
                placeAddress_tv = newPlace.findViewById(R.id.placeAddress_tv);
                mRemoveStage = newPlace.findViewById(R.id.mRemoveStage);

                placeName_tv.setText(entry.getValue().getName());
                placeDescription_tv.setText(entry.getValue().getDescription());
                placeAddress_tv.setText(entry.getValue().getAddress());

                newPlace.setTag(entry.getValue().getName());

                mRemoveStage.setOnClickListener(v13 -> {
                    tappe.remove(newPlace.getTag());
                    linearLayout.removeView(newPlace);

                    if(listStage_title.getVisibility() == View.GONE && tappe.size() == 0)
                        listStage_title.setVisibility(View.VISIBLE);
                });

                linearLayout.addView(newPlace, 0);
            }
        }else {
            listStage_title.setVisibility(View.VISIBLE);
        }
    }

    /**
     *      Check if all fields are compiled correctly.
     *
     *      @return true if all fields are successfully checked, false if there's a problem
     */
    private boolean checkField()
    {
        boolean alright = true;

        if(mLuogo.getText().toString().isEmpty()) {
            luogo_box.setError(getResources().getString(R.string.no_empty_field), false);
            alright = false;
        }

        if(mPuntoIncontro.getText().toString().isEmpty()) {
            punto_box.setError(getResources().getString(R.string.no_empty_field), false);
            alright = false;
        }

        if(mData.getText().toString().isEmpty()) {
            data_box.setError(getResources().getString(R.string.no_empty_field), false);
            alright = false;
        } else{
            Date day;
            try {
                day = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY).parse(mData.getText().toString());

                if(day.before(new Date())){
                    data_box.setError(getResources().getString(R.string.incorrect_date), false);
                    alright = false;
                }
            }catch (Exception e){
                e.getStackTrace();
            }

        }

        if(mOra.getText().toString().isEmpty()) {
            orario_box.setError(getResources().getString(R.string.no_empty_field), false);
            alright = false;
        }

        if(mLingua.getText().toString().isEmpty()) {
            lingua_box.setError(getResources().getString(R.string.no_empty_field), false);
            alright = false;
        }

        if(mDescrizione.getText().toString().isEmpty()) {
            descrizione_itinerario_box.setError(getResources().getString(R.string.no_empty_field), false);
            alright = false;
        }

        if(tappe.size() == 0) {
            listStage_title.setVisibility(View.GONE);
            errorMsg_tv.setVisibility(View.VISIBLE);
            alright = false;
        }

        return alright;
    }

    /**
     * Check if the place already exist.
     *
     * @param coordinates The name of the Place
     * @return true if the field already exist, false if the place doesn't exist
     */
    private boolean placeAlreadyExist(LatLng coordinates)
    {
        for (Map.Entry<String, Stage> entry : tappe.entrySet()) {
            if(coordinates.equals(entry.getValue().getCoordinates()))
                return true;
        }
        return false;
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return super.onSupportNavigateUp();
    }

    private void setItemTitle() {
        MenuItem button = menu.findItem(R.id.createItinerary);

        if(edit_mode)
            button.setTitle("modifica");
        else
            button.setTitle("crea");
    }

    public boolean checkLocationPermission()
    {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObjectSharer.get().remove("edit_itinerary");
    }
}

