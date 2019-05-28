package com.nullpointerexception.cicerone.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.activities.MainActivity;
import com.nullpointerexception.cicerone.components.AuthenticationManager;

public class HomeFragment extends Fragment
{

    public HomeFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        if(getActivity() != null && ((MainActivity) getActivity()).getSupportActionBar() != null)
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.menu_home);

        if(AuthenticationManager.get().getUserLogged() != null)
            ((TextView) view.findViewById(R.id.welcomeText)).setText(
                    "Ciao, " + AuthenticationManager.get().getUserLogged().getName() + " !");
        return view;
    }
}
