package com.nullpointerexception.cicerone.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;
import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.activities.MainActivity;
import com.nullpointerexception.cicerone.activities.ParticipantsActivity;
import com.nullpointerexception.cicerone.activities.ProposedStageActivity;
import com.nullpointerexception.cicerone.components.BackEndInterface;
import com.nullpointerexception.cicerone.components.Itinerary;
import com.nullpointerexception.cicerone.components.ObjectSharer;
import com.nullpointerexception.cicerone.components.Stage;
import com.nullpointerexception.cicerone.components.User;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment
{
    private Button button1, button2;

    public ProfileFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        if(getActivity() != null && ((MainActivity) getActivity()).getSupportActionBar() != null)
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.menu_profile);

        return view;
    }

}
