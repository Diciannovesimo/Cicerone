package com.nullpointerexception.cicerone.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.activities.MainActivity;

public class ProfileFragment extends Fragment
{


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
