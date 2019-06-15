package com.nullpointerexception.cicerone.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
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
import com.kinda.alert.KAlertDialog;
import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.components.AuthenticationManager;
import com.nullpointerexception.cicerone.components.BackEndInterface;
import com.nullpointerexception.cicerone.components.ImageFetcher;
import com.nullpointerexception.cicerone.components.Itinerary;
import com.nullpointerexception.cicerone.components.ObjectSharer;
import com.nullpointerexception.cicerone.components.ProfileImageFetcher;
import com.nullpointerexception.cicerone.components.Stage;
import com.nullpointerexception.cicerone.components.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class ItineraryActivity extends AppCompatActivity {

    private static final String TAG = "It_ItineraryActivity";
    private TextView mDateCard, mPlaceCard, mTimeCard, mLanguageCard, mDescriptionCard, mParticipantsCard, mPriceCard, mCityCard;
    private TextView placeName, placeDescription, mShowLocation;
    private EditText mPlace, mPlaceDesc;
    private TextFieldBoxes place_box;
    private ImageView ciceronePhoto, cityImage, findPosition;
    private Button mItinerary, mPurposePlace, mPartecipantsList, create_stage;
    private com.kinda.mtextfield.TextFieldBoxes descrizione_tappa_box;
    private Stage stage;
    private List<Stage> proposedStages = new ArrayList<>();

    //UI
    private LinearLayout linearLayout;
    private ConstraintLayout constraintLayout;
    private boolean userMode = false;
    private boolean subscribed = false;

    //Istanza itinerario
    private Itinerary itinerary;

    //istanza utente
    private User user;

    //Google maps things
    private List<Place.Field> fields;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private boolean gpsPermission = false;
    private PlacesClient placesClient;
    private FindCurrentPlaceRequest request;
    private PlaceLikelihood MaxPlaceLikelihood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //UI initialization
        initUI();

        //User initialization
        user = AuthenticationManager.get().getUserLogged();

        //Receiver info from test btn
        if(ObjectSharer.get().getSharedObject("show_trip_as_user") != null)
        {
            itinerary = (Itinerary)ObjectSharer.get().getSharedObject("show_trip_as_user");
            ObjectSharer.get().remove("show_trip_as_user");
            userMode = true;
            if(itinerary != null)
                setTextField();
        } else if(ObjectSharer.get().getSharedObject("show_trip_as_cicerone") != null) {
            itinerary = (Itinerary)ObjectSharer.get().getSharedObject("show_trip_as_cicerone");
            ObjectSharer.get().remove("show_trip_as_cicerone");
            if(itinerary != null)
                setTextField();
        }

        toolbar.setTitle("Itinerario");

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
        placesClient = Places.createClient(this);

        // Use the builder to create a FindCurrentPlaceRequest.
        request = FindCurrentPlaceRequest.builder(fields).build();
    }

    private void initUI() {
        mDateCard  = findViewById(R.id.date_card);
        mPlaceCard = findViewById(R.id.place_card);
        mTimeCard = findViewById(R.id.time_card);
        mLanguageCard = findViewById(R.id.language_card);
        mDescriptionCard = findViewById(R.id.description_card);
        mParticipantsCard = findViewById(R.id.partecipants_card);
        mPriceCard = findViewById(R.id.price_card);
        mCityCard = findViewById(R.id.city_card);
        linearLayout = findViewById(R.id.stage_linear_layout);
        ciceronePhoto = findViewById(R.id.ciceronePhoto);
        cityImage = findViewById(R.id.cityImage);
        mItinerary = findViewById(R.id.itinerary_btn);
        constraintLayout = findViewById(R.id.itineraryLayout);
        mPurposePlace = findViewById(R.id.purposePlace_btn);
        mPartecipantsList = findViewById(R.id.partecipantsList_btn);
    }

    private void setTextField() {
        ImageFetcher imageFetcher = new ImageFetcher();

        imageFetcher.findSubject(itinerary.getLocation(), new ImageFetcher.OnImageFoundListener() {
            @Override
            public void onImageFound(String url) {

                ((Activity) cityImage.getContext()).runOnUiThread(() ->
                        Glide.with(cityImage.getContext())
                                .load(url)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(cityImage));
            }

            @Override
            public void onError() {

            }
        });

        mCityCard.setText(itinerary.getLocation());
        mDateCard.setText(itinerary.getDate());
        mPlaceCard.setText(itinerary.getMeetingPlace());
        mTimeCard.setText(itinerary.getMeetingTime());
        if(itinerary.getLanguage() != null)
            mLanguageCard.setText(itinerary.getLanguage());

        if(itinerary.getDescription() != null)
            mDescriptionCard.setText(itinerary.getDescription());

        if(itinerary.getPrice() != 0.0) {
            String price = itinerary.getCurrency() + " " + itinerary.getPrice();
            mPriceCard.setText(price);
        } else {
            mPriceCard.setText("Prezzo non specificato");
        }

        if(itinerary.getMaxParticipants() != 0) {
            if(itinerary.getParticipants() != null){
                String participants = itinerary.getParticipants().size() + "/" +
                        itinerary.getMaxParticipants();
                mParticipantsCard.setText(participants);
            }
        } else {
            if(itinerary.getParticipants() != null)
                mParticipantsCard.setText(String.valueOf(itinerary.getParticipants().size()));
            else
                mParticipantsCard.setText("0");
        }

        new ProfileImageFetcher(this)
                .fetchImageOf(itinerary.getCicerone(), drawable ->
                {
                    ciceronePhoto.setImageDrawable(drawable);
                });

        View redSphere = getLayoutInflater().inflate(R.layout.red_sphere_layout, null);
        linearLayout.addView(redSphere, 0);

        if(itinerary.getStages() != null) {
            for (int i = 0; i < itinerary.getStages().size(); ++i) {
                View stageView = getLayoutInflater().inflate(R.layout.place_layout, null);
                placeName = stageView.findViewById(R.id.placeName);
                placeDescription = stageView.findViewById(R.id.placeDescription);
                mShowLocation = stageView.findViewById(R.id.showStage_btn);

                placeName.setText(itinerary.getStages().get(i).getName());
                placeDescription.setText(itinerary.getStages().get(i).getDescription());

                int finalI = i;
                mShowLocation.setOnClickListener(v -> {
                    LatLng coordinates = itinerary.getStages().get(finalI).getCoordinates();
                    Double lat = coordinates.latitude;
                    Double lng = coordinates.longitude;
                    Intent intent = new Intent(v.getContext(), showPlaceActivity.class);
                    intent.putExtra("latitude", lat);
                    intent.putExtra("longitude", lng);
                    intent.putExtra("marker", itinerary.getStages().get(finalI).getName());
                    startActivity(intent);
                });

                linearLayout.addView(stageView);
            }
        }

        List<User> userList = new ArrayList<>();
        userList = itinerary.getParticipants();

        //Check if user is already a participant of the itinerary
        for (int i = 0; i < userList.size(); ++i) {
            if (user.getId().equals(userList.get(i).getId()))
                subscribed = true;
        }

        if(userMode) {
            if (subscribed) {
                mItinerary.setVisibility(View.VISIBLE);
                mItinerary.setText("Non partecipare");
                mPurposePlace.setVisibility(View.GONE);
                mPartecipantsList.setVisibility(View.GONE);

                View addStageView = getLayoutInflater().inflate(R.layout.add_stage_button, null);

                addStageView.setOnClickListener(v -> {
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

                    });

                    //Listener to open Google Place autocomplete intent
                    place_box.setOnClickListener(v1 -> {
                        Intent intent = new Autocomplete.IntentBuilder(
                                AutocompleteActivityMode.FULLSCREEN, fields)
                                .build(v1.getContext());
                        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
                    });

                    //Listener to capture user's position
                    findPosition.setOnClickListener(v14 -> {

                        ActivityCompat.requestPermissions(this, new String[]{Manifest
                                .permission.ACCESS_FINE_LOCATION}, 1);

                        gpsPermission = checkLocationPermission();

                        if (gpsPermission) {

                            if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
                                placeResponse.addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        FindCurrentPlaceResponse response = task.getResult();
                                        MaxPlaceLikelihood = null;
                                        int counter = 0;

                                        for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                                            if (counter == 0)
                                                MaxPlaceLikelihood = placeLikelihood;

                                            counter++;

                                            if (MaxPlaceLikelihood.getLikelihood() <= placeLikelihood.getLikelihood())
                                                MaxPlaceLikelihood = placeLikelihood;

                                            Log.i(TAG, String.format("Place '%s' has likelihood: %f",
                                                    placeLikelihood.getPlace().getName(),
                                                    placeLikelihood.getLikelihood()));
                                        }
                                        //Set the name of Stage in EditText
                                        mPlace.setText(MaxPlaceLikelihood.getPlace().getName());
                                        //Set attributes of Place in stage
                                        stage = new Stage();
                                        stage.setName(MaxPlaceLikelihood.getPlace().getName());
                                        stage.setCoordinates(MaxPlaceLikelihood.getPlace().getLatLng());
                                        stage.setAddress(MaxPlaceLikelihood.getPlace().getAddress());
                                    } else {
                                        Exception exception = task.getException();
                                        if (exception instanceof ApiException) {
                                            ApiException apiException = (ApiException) exception;
                                            Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                                        }
                                    }
                                });
                            }
                        } else {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        }

                    });

                    //Check if the number of character in description field is more than 250 character
                    descrizione_tappa_box.setSimpleTextChangeWatcher((theNewText, isError) -> {
                        if (theNewText.length() > 250)
                            descrizione_tappa_box.setError(getResources().getString(R.string.max_char), false);
                    });

                    //Listener to create a new place for a itinerary
                    create_stage.setOnClickListener(v12 -> {
                        //Check if the number of character in description field is more than 250 character
                        if (mPlaceDesc.getText().toString().length() > 250) {

                            descrizione_tappa_box.setError(getResources().getString(R.string.insert_shorter_desc), false);

                            //Check if the description is empty
                        } else if (mPlaceDesc.getText().toString().isEmpty()) {

                            descrizione_tappa_box.setError(getResources().getString(R.string.no_desc), false);

                            //Check if the name's field is empty
                        } else if (mPlace.getText().toString().isEmpty()) {

                            place_box.setError(getResources().getString(R.string.no_place_name), false);

                            //Check if the place already exist
                        } else if (placeAlreadyExist(stage.getCoordinates())) {

                            runOnUiThread(() -> {
                                // Show error message
                                new KAlertDialog(mView.getContext(), KAlertDialog.ERROR_TYPE)
                                        .setTitleText(getResources().getString(R.string.error_dialog_title))
                                        .setContentText(getResources().getString(R.string.error_dialog_content))
                                        .setConfirmText(getResources().getString(R.string.error_dialog_confirmText))
                                        .show();
                            });
                        } else {
                            stage.setDescription(mPlaceDesc.getText().toString());
                            itinerary.addProposedStage(stage);

                            //TODO: Luca guarda qui ;)
                            try {
                                BackEndInterface.get().storeEntity(itinerary, new BackEndInterface.OnOperationCompleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onError() {
                                        Toast.makeText(getApplicationContext(), "Impossibile caricare la tappa", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    dialog.show();
                });

                mItinerary.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<User> userList = itinerary.getParticipants();
                        for(int i = 0; i < userList.size(); ++i) {
                            if(user.getId().equals(userList.get(i).getId()))
                                userList.remove(i);
                        }

                        itinerary.setParticipants(userList);
                        BackEndInterface.get().removeEntity(itinerary, new BackEndInterface.OnOperationCompleteListener() {
                            @Override
                            public void onSuccess() {
                                BackEndInterface.get().storeEntity(itinerary, new BackEndInterface.OnOperationCompleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        new KAlertDialog(v.getContext())
                                                .setTitleText("Complimenti")
                                                .setContentText("Ti sei disinscritto dalla gita")
                                                .setConfirmText("Ok")
                                                .setConfirmClickListener(new KAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(KAlertDialog kAlertDialog) {
                                                        ObjectSharer.get().shareObject("show_trip_as_user", itinerary);
                                                        v.getContext().startActivity(new Intent(getApplicationContext(), ItineraryActivity.class));
                                                        finish();
                                                    }
                                                }).show();
                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
                            }

                            @Override
                            public void onError() {

                            }
                        });

                    }
                });

                linearLayout.addView(addStageView);
            }else {
                mItinerary.setVisibility(View.VISIBLE);
                mPurposePlace.setVisibility(View.GONE);
                mPartecipantsList.setVisibility(View.GONE);

                View addStageView = getLayoutInflater().inflate(R.layout.add_stage_button, null);

                addStageView.setOnClickListener(v -> {
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

                    });

                    //Listener to open Google Place autocomplete intent
                    place_box.setOnClickListener(v1 -> {
                        Intent intent = new Autocomplete.IntentBuilder(
                                AutocompleteActivityMode.FULLSCREEN, fields)
                                .build(v1.getContext());
                        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
                    });

                    //Listener to capture user's position
                    findPosition.setOnClickListener(v14 -> {

                        ActivityCompat.requestPermissions(this, new String[]{Manifest
                                .permission.ACCESS_FINE_LOCATION}, 1);

                        gpsPermission = checkLocationPermission();

                        if (gpsPermission) {

                            if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
                                placeResponse.addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        FindCurrentPlaceResponse response = task.getResult();
                                        MaxPlaceLikelihood = null;
                                        int counter = 0;

                                        for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                                            if (counter == 0)
                                                MaxPlaceLikelihood = placeLikelihood;

                                            counter++;

                                            if (MaxPlaceLikelihood.getLikelihood() <= placeLikelihood.getLikelihood())
                                                MaxPlaceLikelihood = placeLikelihood;

                                            Log.i(TAG, String.format("Place '%s' has likelihood: %f",
                                                    placeLikelihood.getPlace().getName(),
                                                    placeLikelihood.getLikelihood()));
                                        }
                                        //Set the name of Stage in EditText
                                        mPlace.setText(MaxPlaceLikelihood.getPlace().getName());
                                        //Set attributes of Place in stage
                                        stage = new Stage();
                                        stage.setName(MaxPlaceLikelihood.getPlace().getName());
                                        stage.setCoordinates(MaxPlaceLikelihood.getPlace().getLatLng());
                                        stage.setAddress(MaxPlaceLikelihood.getPlace().getAddress());
                                    } else {
                                        Exception exception = task.getException();
                                        if (exception instanceof ApiException) {
                                            ApiException apiException = (ApiException) exception;
                                            Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                                        }
                                    }
                                });
                            }
                        } else {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        }

                    });

                    //Check if the number of character in description field is more than 250 character
                    descrizione_tappa_box.setSimpleTextChangeWatcher((theNewText, isError) -> {
                        if (theNewText.length() > 250)
                            descrizione_tappa_box.setError(getResources().getString(R.string.max_char), false);
                    });

                    //Listener to create a new place for a itinerary
                    create_stage.setOnClickListener(v12 -> {
                        //Check if the number of character in description field is more than 250 character
                        if (mPlaceDesc.getText().toString().length() > 250) {

                            descrizione_tappa_box.setError(getResources().getString(R.string.insert_shorter_desc), false);

                            //Check if the description is empty
                        } else if (mPlaceDesc.getText().toString().isEmpty()) {

                            descrizione_tappa_box.setError(getResources().getString(R.string.no_desc), false);

                            //Check if the name's field is empty
                        } else if (mPlace.getText().toString().isEmpty()) {

                            place_box.setError(getResources().getString(R.string.no_place_name), false);

                            //Check if the place already exist
                        } else if (placeAlreadyExist(stage.getCoordinates())) {

                            runOnUiThread(() -> {
                                // Show error message
                                new KAlertDialog(mView.getContext(), KAlertDialog.ERROR_TYPE)
                                        .setTitleText(getResources().getString(R.string.error_dialog_title))
                                        .setContentText(getResources().getString(R.string.error_dialog_content))
                                        .setConfirmText(getResources().getString(R.string.error_dialog_confirmText))
                                        .show();
                            });
                        } else {
                            stage.setDescription(mPlaceDesc.getText().toString());
                            itinerary.addProposedStage(stage);

                            //TODO: Luca guarda qui ;)
                            try {
                                BackEndInterface.get().storeEntity(itinerary, new BackEndInterface.OnOperationCompleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onError() {
                                        Toast.makeText(getApplicationContext(), "Impossibile caricare la tappa", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    dialog.show();
                });

                mItinerary.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itinerary.addPartecipant(user);
                        user.addItinerary(itinerary);

                        BackEndInterface.get().removeEntity(itinerary, new BackEndInterface.OnOperationCompleteListener() {
                            @Override
                            public void onSuccess() {
                                BackEndInterface.get().storeEntity(itinerary, new BackEndInterface.OnOperationCompleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        BackEndInterface.get().storeEntity(user, new BackEndInterface.OnOperationCompleteListener() {
                                            @Override
                                            public void onSuccess() {
                                                new KAlertDialog(v.getContext())
                                                        .setTitleText("Complimenti")
                                                        .setContentText("Ti sei iscritto a questa visita!")
                                                        .setConfirmText("Ok")
                                                        .setConfirmClickListener(new KAlertDialog.OnSweetClickListener() {
                                                            @Override
                                                            public void onClick(KAlertDialog kAlertDialog) {
                                                                ObjectSharer.get().shareObject("show_trip_as_user", itinerary);
                                                                v.getContext().startActivity(new Intent(getApplicationContext(), ItineraryActivity.class));
                                                                finish();
                                                            }
                                                        }).show();
                                            }

                                            @Override
                                            public void onError() {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
                            }

                            @Override
                            public void onError() {

                            }
                        });
                    }
                });

                linearLayout.addView(addStageView);
            }
        }else {
            mItinerary.setVisibility(View.GONE);

            mPurposePlace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*ObjectSharer.get().shareObject("lista_proposte", itinerary);
                    Itinerary itinerary = (Itinerary)ObjectSharer.get().getSharedObject("lista_proposte");*/
                }
            });

            mPartecipantsList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

    }

    public boolean checkLocationPermission()
    {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Check if the place already exist.
     *
     * @param coordinates The name of the Place
     * @return true if the field already exist, false if the place doesn't exist
     */
    private boolean placeAlreadyExist(LatLng coordinates)
    {
        for(int i = 0; i < itinerary.getStages().size(); ++i) {
            if(coordinates.equals(itinerary.getStages().get(i)))
                return true;
        }
        return false;
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
                stage = new Stage(place.getName(), place.getAddress(), place.getLatLng());
                mPlace.setText(stage.getName());
            }
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Log.i("it_view", status.getStatusMessage());
        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.
        }
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        if(ObjectSharer.get().getSharedObject("show_trip_as_user") != null)
            ObjectSharer.get().remove("show_trip_as_user");
        else if(ObjectSharer.get().getSharedObject("show_trip_as_cicerone") != null)
            ObjectSharer.get().remove("show_trip_as_cicerone");

        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(ObjectSharer.get().getSharedObject("show_trip_as_user") != null)
            ObjectSharer.get().remove("show_trip_as_user");
        else if(ObjectSharer.get().getSharedObject("show_trip_as_cicerone") != null)
            ObjectSharer.get().remove("show_trip_as_cicerone");
    }
}
