package com.nullpointerexception.cicerone.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.activities.ItineraryActivity;
import com.nullpointerexception.cicerone.activities.LoginActivity;
import com.nullpointerexception.cicerone.activities.MainActivity;

public class ItinerariesListFragment extends Fragment
{

    /*
            Views
     */
    private RecyclerView itinerariesList;
    private FloatingActionButton newItineraryButton;

    public ItinerariesListFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_itineraries_list, container, false);

        if(getActivity() != null && ((MainActivity) getActivity()).getSupportActionBar() != null)
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.menu_itineraries_list);

        itinerariesList = view.findViewById(R.id.itinerariesListContainer);
        newItineraryButton = view.findViewById(R.id.newItineraryButton);

        newItineraryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), ItineraryActivity.class));
            }
        });

        return view;
    }
}
