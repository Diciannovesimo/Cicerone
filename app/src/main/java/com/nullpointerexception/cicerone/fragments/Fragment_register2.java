package com.nullpointerexception.cicerone.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.nullpointerexception.cicerone.R;


public class Fragment_register2 extends Fragment
{

    private EditText nameField, surnameField, date_birthField;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register2, container, false);

        nameField = view.findViewById(R.id.nameTextField);
        surnameField = view.findViewById(R.id.surnameTextField);
        date_birthField = view.findViewById(R.id.dateTextField);

        return view;
    }

    public EditText getNameField() {
        return nameField;
    }

    public EditText getSurnameField() {
        return surnameField;
    }

    public EditText getDate_birthField() {
        return date_birthField;
    }
}
