package com.nullpointerexception.cicerone.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.activities.FindItineraryActivty;
import com.nullpointerexception.cicerone.activities.ItineraryCreationActivity;
import com.nullpointerexception.cicerone.activities.MainActivity;
import com.nullpointerexception.cicerone.components.AuthenticationManager;
import com.nullpointerexception.cicerone.components.Blocker;

public class HomeFragment extends Fragment
{

    public HomeFragment() { }

    public View buttonCreateItinerary, buttonFindItinerary;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        buttonCreateItinerary = view.findViewById(R.id.buttonCreateItinerary);
        buttonFindItinerary = view.findViewById(R.id.buttonFindItinerary);

        if(getActivity() != null && ((MainActivity) getActivity()).getSupportActionBar() != null)
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.menu_home);

        if(AuthenticationManager.get().getUserLogged() != null)
            ((TextView) view.findViewById(R.id.welcomeText)).setText(
                    getResources().getString(R.string.homePre) + " " +
                            AuthenticationManager.get().getUserLogged().getName() + " !");

        buttonCreateItinerary.setOnClickListener(new View.OnClickListener() {
            private Blocker mBlocker = new Blocker();

            @Override
            public void onClick(View v) {
                if(!mBlocker.block()) {
                    startActivity(new Intent(getContext(), ItineraryCreationActivity.class));
                }
            }
        });

        buttonFindItinerary.setOnClickListener(new View.OnClickListener() {
            private Blocker mBlocker = new Blocker();

            @Override
            public void onClick(View v) {
                if(!mBlocker.block()) {
                    startActivity(new Intent(getContext(), FindItineraryActivty.class));
                }
            }
        });

        return view;
    }
}
