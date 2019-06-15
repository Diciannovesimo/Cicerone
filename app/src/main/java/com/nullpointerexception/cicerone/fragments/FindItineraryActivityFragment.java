package com.nullpointerexception.cicerone.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.kinda.alert.KAlertDialog;
import com.nullpointerexception.cicerone.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FindItineraryActivityFragment extends Fragment
{

    /*
            UI Components
     */
    private TextView findLocation, insertDate, findButton;

    /*
            Vars
     */
    private List<Place.Field> fields;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.find_itinerary_activity_fragment, container, false);

        //  Set UI
        findLocation = view.findViewById(R.id.newTripFindLocation);
        insertDate = view.findViewById(R.id.newTripInsertDate);
        findButton = view.findViewById(R.id.startSearchButton);

        //  Initialization

        //Initialize Google Places API
        Places.initialize(view.getContext(), getResources().getString(R.string.place_KEY));
        fields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        findLocation.setOnClickListener(v ->
        {
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, fields)
                    .build(findLocation.getContext());
            startActivityForResult(intent, 123);
        });

        insertDate.setOnClickListener(v ->
        {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            new DatePickerDialog(v.getContext(), R.style.DialogTheme, (v2, yearS, monthS, dayOfMonth) ->
            {
                String dateString = "";

                if(dayOfMonth < 10)
                    dateString += "0" + dayOfMonth + "/";
                else
                    dateString = dayOfMonth + "/";

                if((monthS + 1) < 10)
                    dateString += "0" + (monthS + 1) + "/" + yearS;
                else
                    dateString += (monthS + 1) + "/" + yearS;

                insertDate.setText(dateString);

            }, year, month, day)
            .show();
        });

        findButton.setOnClickListener(v ->
        {
            if(checkFields())
            {
                Date date = null;
                try
                {
                    date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(insertDate.getText().toString());
                }
                catch (ParseException e)
                {
                    Log.e("FindItinerary", e.toString());
                    return;
                }
                String formattedDate = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(date);

                if(getActivity() != null && getActivity() instanceof OnUIInteractionListener)
                    ((OnUIInteractionListener) getActivity())
                            .onButtonSearchPressed(findLocation.getText().toString(), formattedDate);
            }
            else
            {
                new KAlertDialog(findButton.getContext(), KAlertDialog.ERROR_TYPE)
                        .setTitleText( getResources().getString(R.string.error) )
                        .setContentText( getResources().getString(R.string.fields_not_inserted) )
                        .setConfirmText("OK")
                        .show();
            }
        });

        View background = view.findViewById(R.id.findItineraryBackground);
        background.setAlpha(0f);
        background.animate().alphaBy(1f).setDuration(1000).start();

        return view;
    }

    private boolean checkFields()
    {
        boolean alright = true;

        if(findButton.getText().toString().isEmpty())
            alright = false;

        if(insertDate.getText().toString().isEmpty())
            alright = false;

        return alright;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 123)
        if (resultCode == Activity.RESULT_OK)
        {
            Place place = Autocomplete.getPlaceFromIntent(data);
            findLocation.setText(place.getName());
        }

    }

    public interface OnUIInteractionListener { void onButtonSearchPressed(String place, String date); }

}
