package com.nullpointerexception.cicerone.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.activities.MainActivity;
import com.nullpointerexception.cicerone.activities.ParticipantsActivity;
import com.nullpointerexception.cicerone.activities.ProposedStageActivity;

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

        button1 = view.findViewById(R.id.button1);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(v.getContext(), ParticipantsActivity.class));

            }
        });

        button2 = view.findViewById(R.id.button2);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(v.getContext(), ProposedStageActivity.class));

            }
        });

        return view;
    }

}
