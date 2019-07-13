package com.nullpointerexception.cicerone.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import com.nullpointerexception.cicerone.components.Blocker;
import com.nullpointerexception.cicerone.components.ImageFetcher;
import com.nullpointerexception.cicerone.components.Itinerary;
import com.nullpointerexception.cicerone.components.ObjectSharer;
import com.nullpointerexception.cicerone.components.ProfileImageFetcher;
import com.nullpointerexception.cicerone.components.Stage;
import com.nullpointerexception.cicerone.components.User;
import com.nullpointerexception.cicerone.components.UserNotification;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * ItineraryActivity
 *
 * This class allow user to show itinerary information
 *
 * This class use = {@link Stage} {@link User} {@link Itinerary} {@link ObjectSharer} {@link AuthenticationManager}
 * {@link BackEndInterface} {@link Blocker} {@link ImageFetcher} {@link ProfileImageFetcher}
 * {@link UserNotification}
 *
 * @author Claudio
 */
public class ItineraryActivity extends AppCompatActivity
{

    private static final String TAG = "It_ItineraryActivity";
    private final int PROPOSED_STAGES_CODE = 321;
    private final int PARTICIPANT_CODE = 123;
    private TextView mDateCard, mPlaceCard, mTimeCard, mLanguageCard, mDescriptionCard, mParticipantsCard, mPriceCard;
    private EditText mPlace, mPlaceDesc;
    private TextFieldBoxes place_box;
    private ImageView ciceronePhoto, cityImage, findPosition;
    private Button mItinerary, mPurposePlace, mPartecipantsList, create_stage;
    private com.kinda.mtextfield.TextFieldBoxes descrizione_tappa_box;
    private Stage stage;

    //UI
    private LinearLayout linearLayout;
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
    protected void onCreate(Bundle savedInstanceState)
    {
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

        if(itinerary != null)
        toolbar.setTitle(itinerary.getLocation());

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

    /**
     * Initialize itinerary activity fields
     */
    private void initUI() {
        mDateCard  = findViewById(R.id.date_card);
        mPlaceCard = findViewById(R.id.place_card);
        mTimeCard = findViewById(R.id.time_card);
        mLanguageCard = findViewById(R.id.language_card);
        mDescriptionCard = findViewById(R.id.description_card);
        mParticipantsCard = findViewById(R.id.partecipants_card);
        mPriceCard = findViewById(R.id.price_card);
        linearLayout = findViewById(R.id.stage_linear_layout);
        ciceronePhoto = findViewById(R.id.ciceronePhoto);
        cityImage = findViewById(R.id.cityImage);
        mItinerary = findViewById(R.id.itinerary_btn);
        mPurposePlace = findViewById(R.id.purposePlace_btn);
        mPartecipantsList = findViewById(R.id.partecipantsList_btn);
    }

    /**
     *    Set text in activity field
     */
    private void setTextField()
    {
        ImageFetcher imageFetcher = new ImageFetcher();

        String keyword = itinerary.getLocation() + " " + getResources().getString(R.string.city);

        if(itinerary.getLocation() != null &&
                itinerary.getLocation().trim().equalsIgnoreCase("bitritto"))
            keyword = "corvo gigante bitritto";

        Drawable resource = (Drawable) ObjectSharer.get().getSharedObject("resource_" + keyword);
        if(resource == null)
        {
            imageFetcher.findSubject(keyword, new ImageFetcher.OnImageFoundListener()
            {
                @Override
                public void onImageFound(String url)
                {
                    ((Activity) cityImage.getContext()).runOnUiThread(() ->
                            Glide.with(getApplicationContext())
                                    .load(url)
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(cityImage));
                }

                @Override
                public void onError() { }
            });
        }
        else
            cityImage.setImageDrawable(resource);

        try {
            Date day = new SimpleDateFormat("yyyy/MM/dd", Locale.ITALY).parse(itinerary.getDate());
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);
            String dayString = formatter.format(day);
            mDateCard.setText(dayString);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        mPlaceCard.setText(itinerary.getMeetingPlace());
        mTimeCard.setText(itinerary.getMeetingTime());

        if(itinerary.getLanguage() != null)
            mLanguageCard.setText(itinerary.getLanguage());

        if(itinerary.getDescription() != null)
            mDescriptionCard.setText(itinerary.getDescription());

        if(itinerary.getPrice() != 0.0) {
            final String COMMA_SEPERATED = "###,##0.00";
            DecimalFormat decimalFormat = new DecimalFormat(COMMA_SEPERATED);
            String price = itinerary.getCurrency() + " " + decimalFormat.format(itinerary.getPrice());
            mPriceCard.setText(price);
        } else {
            mPriceCard.setText("Prezzo non specificato");
        }

        updateParticipants();

        new ProfileImageFetcher(this)
                .fetchImageOf(itinerary.getCicerone(), drawable -> {
                    ciceronePhoto.setImageDrawable(drawable);
                });

        ciceronePhoto.setOnClickListener(new View.OnClickListener() {
            private Blocker mBlocker = new Blocker();

            @Override
            public void onClick(View v) {
                if (!mBlocker.block()) {
                    Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                    intent.putExtra("id_cicerone_to_show", itinerary.getCicerone().getId());
                    startActivity(intent);
                }
            }
        });

        refreshStagesList();

        List<User> userList;
        userList = itinerary.getParticipants();

        //Check if user is already a participant of the itinerary
        for (int i = 0; i < userList.size(); ++i) {
            if (user.getId().equals(userList.get(i).getId())) {
                subscribed = true;
                break;
            }
        }

        //Setting dell'interfaccia
        if(userMode) {

            View addStageView = getLayoutInflater().inflate(R.layout.add_stage_button, null);

            addStageView.setOnClickListener(new View.OnClickListener() {
                private Blocker mBlocker = new Blocker();

                @Override
                public void onClick(View v) {
                    if (!mBlocker.block()) {
                        //New layout for place's dialog
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ItineraryActivity.this);
                        View mView = getLayoutInflater().inflate(R.layout.activity_dialog_tappe, null);

                        //Init field of place's layout
                        descrizione_tappa_box = mView.findViewById(R.id.feedback_box);
                        mPlace = mView.findViewById(R.id.place_et);
                        mPlaceDesc = mView.findViewById(R.id.placeDesc_et);
                        place_box = mView.findViewById(R.id.place_box);
                        create_stage = mView.findViewById(R.id.createStage_btn);
                        findPosition = mView.findViewById(R.id.findPosition);

                        mPlace.setEnabled(false);

                        //Create a new dialog
                        mBuilder.setView(mView);
                        AlertDialog dialog = mBuilder.create();

                        //Listener to open Google Place autocomplete intent
                        place_box.setOnClickListener(new View.OnClickListener() {
                            private Blocker mBlocker = new Blocker();

                            @Override
                            public void onClick(View v) {
                                if (!mBlocker.block()) {
                                    Intent intent = new Autocomplete.IntentBuilder(
                                            AutocompleteActivityMode.FULLSCREEN, fields)
                                            .build(v.getContext());
                                    startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
                                }
                            }
                        });

                        //Listener to capture user's position
                        findPosition.setOnClickListener(new View.OnClickListener() {
                            private Blocker mBlocker = new Blocker();

                            @Override
                            public void onClick(View v) {
                                if (!mBlocker.block()) {
                                    ActivityCompat.requestPermissions(ItineraryActivity.this, new String[]{Manifest
                                            .permission.ACCESS_FINE_LOCATION}, 1);

                                    gpsPermission = checkLocationPermission();

                                    if (gpsPermission) {

                                        if (ContextCompat.checkSelfPermission(v.getContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
                                    } else
                                        ActivityCompat.requestPermissions(ItineraryActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                                }
                            }
                        });

                        //Check if the number of character in description field is more than 250 character
                        descrizione_tappa_box.setSimpleTextChangeWatcher((theNewText, isError) -> {
                            if (theNewText.length() > 250)
                                descrizione_tappa_box.setError(getResources().getString(R.string.max_char), false);
                        });

                        //Listener to create a new place for a itinerary
                        create_stage.setOnClickListener(new View.OnClickListener() {
                            private Blocker mBlocker = new Blocker();

                            @Override
                            public void onClick(View v) {
                                if (!mBlocker.block()) {
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

                                        try {
                                            BackEndInterface.get().storeEntity(itinerary, new BackEndInterface.OnOperationCompleteListener() {
                                                @Override
                                                public void onSuccess()
                                                {
                                                    /*
                                                          Send notification
                                                     */
                                                    UserNotification notification = new UserNotification(
                                                            itinerary.getCicerone().getId()
                                                    );
                                                    notification.setIdItinerary(itinerary.getId());
                                                    notification.setTitle("Nuova tappa");
                                                    notification.setContent(user.getDisplayName() + " ha proposto una nuova tappa al tuo itinerario per " +
                                                            itinerary.getLocation());
                                                    BackEndInterface.get().storeEntity(notification);

                                                    dialog.dismiss();
                                                }

                                                @Override
                                                public void onError() {
                                                    Toast.makeText(getApplicationContext(), "Impossibile caricare la tappa", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } catch (Exception e) {
                                            Log.e(TAG, e.toString());
                                        }
                                    }
                                }
                            }
                        });
                        dialog.show();
                    }
                }
            });

            linearLayout.addView(addStageView);

            if(subscribed) {
                mItinerary.setVisibility(View.VISIBLE);
                Date itineraryDate = new Date();
                try {
                    itineraryDate = new SimpleDateFormat("yyyy/MM/dd", Locale.ITALY).parse(itinerary.getDate());
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }

                Date today = new Date(System.currentTimeMillis());

                if(itineraryDate.before(today))
                    mItinerary.setText("Cancella");
                else
                    mItinerary.setText("Non partecipare");

                mPurposePlace.setVisibility(View.GONE);
                mPartecipantsList.setVisibility(View.GONE);
            }else {
                if(itinerary.getMaxParticipants() != 0) {
                    if (itinerary.getParticipants().size() < itinerary.getMaxParticipants()) {

                        mItinerary.setVisibility(View.VISIBLE);
                    }else {

                        mItinerary.setVisibility(View.GONE);
                        mParticipantsCard.setTextColor(getResources().getColor(R.color.md_red_400));
                    }
                }else
                    mItinerary.setVisibility(View.VISIBLE);

                mPurposePlace.setVisibility(View.GONE);
                mPartecipantsList.setVisibility(View.GONE);
            }
        }else {
            mItinerary.setVisibility(View.GONE);

            if(itinerary.getProposedStages().size() != 0) {
                mPurposePlace.setOnClickListener(new View.OnClickListener() {
                    private Blocker mBlocker = new Blocker();

                    @Override
                    public void onClick(View v) {
                        if (!mBlocker.block()) {
                            ObjectSharer.get().shareObject("lista_proposte", itinerary);
                            startActivityForResult(new Intent(v.getContext(), ProposedStageActivity.class),
                                    PROPOSED_STAGES_CODE);
                        }
                    }
                });
            } else {
                mPurposePlace.setVisibility(View.GONE);
            }

            if(itinerary.getParticipants().size() != 0) {

                mPartecipantsList.setOnClickListener(new View.OnClickListener() {
                    private Blocker mBlocker = new Blocker();

                    @Override
                    public void onClick(View v) {
                        if (!mBlocker.block()) {
                            ObjectSharer.get().shareObject("lista_partecipanti", itinerary);
                            startActivityForResult(new Intent(v.getContext(), ParticipantsActivity.class),
                                    PARTICIPANT_CODE);
                        }
                    }
                });
            } else{
                mPartecipantsList.setVisibility(View.GONE);
            }
        }

        //Listener pulsante partecipazione
        mItinerary.setOnClickListener(new View.OnClickListener() {
            private Blocker mBlocker = new Blocker();

            @Override
            public void onClick(View v) {
                if (!mBlocker.block(2000)) {
                    if(userMode) {
                        if(subscribed) {
                            if(mItinerary.getText().toString().equals("Cancella")) {
                                for(int i = 0; i < user.getItineraries().size(); ++i) {
                                    if(itinerary.getId().equals(user.getItineraries().get(i).getId())) {
                                        user.removeItinerary(i);

                                        new KAlertDialog(v.getContext())
                                                .setTitleText("Itinerario cancellato")
                                                .setContentText("Hai cancellato con successo l'itinerario!")
                                                .setConfirmText("Ok")
                                                .setCancelClickListener(kAlertDialog -> finish()).show();
                                    }
                                }

                                BackEndInterface.get().removeEntity(user, new BackEndInterface.OnOperationCompleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        BackEndInterface.get().storeEntity(user);
                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
                            } else {

                                List<User> userList1 = itinerary.getParticipants();
                                for (int i = 0; i < userList1.size(); ++i) {
                                    if (user.getId().equals(userList1.get(i).getId()))
                                        userList1.remove(i);
                                }
                                itinerary.setParticipants(userList1);

                                for (int i = 0; i < user.getItineraries().size(); ++i) {
                                    if (itinerary.getId().equals(user.getItineraries().get(i).getId()))
                                        user.removeItinerary(i);
                                }

                                BackEndInterface.get().removeEntity(itinerary,
                                        new BackEndInterface.OnOperationCompleteListener() {
                                            @Override
                                            public void onSuccess() {
                                                BackEndInterface.get().storeEntity(itinerary,
                                                        new BackEndInterface.OnOperationCompleteListener() {
                                                            @Override
                                                            public void onSuccess() {
                                                                BackEndInterface.get().removeEntity(user);
                                                                BackEndInterface.get().storeEntity(user);
                                                                KAlertDialog kAlertDialog = new KAlertDialog(v.getContext())
                                                                        .setTitleText("Disiscritto")
                                                                        .setContentText("Ti sei disinscritto dalla gita")
                                                                        .setConfirmText("Ok");

                                                                kAlertDialog.setConfirmClickListener(kAlertDialog13 -> {
                                                                    mItinerary.setText("Partecipa");
                                                                    subscribed = false;
                                                                    updateParticipants();
                                                                    kAlertDialog13.dismissWithAnimation();
                                                                });
                                                                kAlertDialog.show();
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
                        }else {
                            if (itinerary.getMaxParticipants() != 0) {
                                if (itinerary.getParticipants().size() < itinerary.getMaxParticipants()) {

                                    itinerary.addPartecipant(user);
                                    user.addItinerary(itinerary);

                                    BackEndInterface.get().removeEntity(itinerary,
                                            new BackEndInterface.OnOperationCompleteListener() {
                                                @Override
                                                public void onSuccess() {
                                                    BackEndInterface.get().storeEntity(itinerary,
                                                            new BackEndInterface.OnOperationCompleteListener() {
                                                                @Override
                                                                public void onSuccess() {
                                                                    BackEndInterface.get().storeEntity(user,
                                                                            new BackEndInterface.OnOperationCompleteListener() {
                                                                                @Override
                                                                                public void onSuccess() {
                                                    /*
                                                        Send notification
                                                    */
                                                                                    UserNotification notification = new UserNotification(
                                                                                            itinerary.getCicerone().getId()
                                                                                    );
                                                                                    notification.setIdItinerary(itinerary.getId());
                                                                                    notification.setTitle("Nuovo partecipante");
                                                                                    notification.setContent(user.getDisplayName() + " si è unito al tuo itinerario per " +
                                                                                            itinerary.getLocation());
                                                                                    BackEndInterface.get().storeEntity(notification);

                                                                                    KAlertDialog kAlertDialog = new KAlertDialog(v.getContext())
                                                                                            .setTitleText("Complimenti")
                                                                                            .setContentText("Ti sei iscritto a" +
                                                                                                    " questa visita!")
                                                                                            .setConfirmText("Ok");

                                                                                    kAlertDialog.setConfirmClickListener(kAlertDialog12 -> {
                                                                                        subscribed = true;
                                                                                        mItinerary.setText("Non partecipare");
                                                                                        updateParticipants();
                                                                                        kAlertDialog12.dismissWithAnimation();
                                                                                    });
                                                                                    kAlertDialog.show();

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
                            }else {
                                itinerary.addPartecipant(user);
                                user.addItinerary(itinerary);

                                BackEndInterface.get().removeEntity(itinerary,
                                        new BackEndInterface.OnOperationCompleteListener() {
                                            @Override
                                            public void onSuccess() {
                                                BackEndInterface.get().storeEntity(itinerary,
                                                        new BackEndInterface.OnOperationCompleteListener() {
                                                            @Override
                                                            public void onSuccess() {
                                                                BackEndInterface.get().storeEntity(user,
                                                                        new BackEndInterface.OnOperationCompleteListener() {
                                                                            @Override
                                                                            public void onSuccess()
                                                                            {
                                                                                /*
                                                                                     Send notification
                                                                                 */
                                                                                UserNotification notification = new UserNotification(
                                                                                        itinerary.getCicerone().getId()
                                                                                );
                                                                                notification.setIdItinerary(itinerary.getId());
                                                                                notification.setTitle("Nuovo partecipante");
                                                                                notification.setContent(user.getDisplayName() + " si è unito al tuo itinerario per " +
                                                                                        itinerary.getLocation());
                                                                                BackEndInterface.get().storeEntity(notification);

                                                                                KAlertDialog kAlertDialog = new KAlertDialog(v.getContext())
                                                                                        .setTitleText("Complimenti")
                                                                                        .setContentText("Ti sei iscritto a" +
                                                                                                " questa visita!")
                                                                                        .setConfirmText("Ok");

                                                                                kAlertDialog.setConfirmClickListener(kAlertDialog1 -> {
                                                                                    subscribed = true;
                                                                                    mItinerary.setText("Non partecipare");
                                                                                    updateParticipants();
                                                                                    kAlertDialog1.dismissWithAnimation();
                                                                                });
                                                                                kAlertDialog.show();
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
                        }
                    }
                }
            }
        });
    }

    /**
     * Refresh activity for new stages
     */
    public void refreshStagesList() {
        linearLayout.removeAllViews();

        View redSphere = getLayoutInflater().inflate(R.layout.red_sphere_layout, null);
        linearLayout.addView(redSphere, 0);

        if(itinerary != null && itinerary.getStages() != null)
        {
            for (int i = 0; i < itinerary.getStages().size(); ++i)
            {
                View stageView = getLayoutInflater().inflate(R.layout.place_layout, null);
                TextView placeName = stageView.findViewById(R.id.placeName);
                TextView placeDescription = stageView.findViewById(R.id.placeDescription);
                TextView mShowLocation = stageView.findViewById(R.id.showStage_btn);

                placeName.setText(itinerary.getStages().get(i).getName());
                placeDescription.setText(itinerary.getStages().get(i).getDescription());

                int finalI = i;

                mShowLocation.setOnClickListener(new View.OnClickListener() {
                    private Blocker mBlocker = new Blocker();

                    @Override
                    public void onClick(View v) {
                        if (!mBlocker.block()) {
                            LatLng coordinates = itinerary.getStages().get(finalI).getCoordinates();
                            Double lat = coordinates.latitude;
                            Double lng = coordinates.longitude;
                            Intent intent = new Intent(v.getContext(), ShowPlaceActivity.class);
                            intent.putExtra("latitude", lat);
                            intent.putExtra("longitude", lng);
                            intent.putExtra("marker", itinerary.getStages().get(finalI).getName());
                            startActivity(intent);
                        }
                    }
                });

                linearLayout.addView(stageView);
            }
        }
    }

    /**
     * Update the number of participants after the inscription
     */
    public void updateParticipants() {
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
    }

    /**
     * Check if the app has the GPS access permission
     *
     * @return res: Value of return of the check
     */
    public boolean checkLocationPermission() {
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
            if (coordinates.equals(itinerary.getStages().get(i).getCoordinates()))
                return true;
        }

        for(int i = 0; i < itinerary.getProposedStages().size(); ++i) {
            if (coordinates.equals(itinerary.getProposedStages().get(i).getCoordinates()))
                return true;
        }
        return false;
    }

    /**
     * Used to receive from Google Place autocomplete intent.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                stage = new Stage(place.getName(), place.getAddress(), place.getLatLng());
                mPlace.setText(stage.getName());
            }
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Log.i("it_view", status.getStatusMessage());
        }

        if(requestCode == PROPOSED_STAGES_CODE && resultCode == RESULT_OK)
        {
            itinerary = (Itinerary) ObjectSharer.get().getSharedObject("show_trip_as_cicerone");
            refreshStagesList();
        }

        if(requestCode == PARTICIPANT_CODE && resultCode == RESULT_OK)
        {
            itinerary = (Itinerary) ObjectSharer.get().getSharedObject("show_trip_as_cicerone");
            refreshStagesList();
        }
    }

    /**
     * Catch the click on back arrow and remove object on objectSherer
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
