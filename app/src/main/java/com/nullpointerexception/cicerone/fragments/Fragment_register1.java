package com.nullpointerexception.cicerone.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.nullpointerexception.cicerone.R;

/**
 * Fragment_register1
 *
 * Allow insertion of email, password e confirm password
 */
public class Fragment_register1 extends Fragment
{

    private EditText emailField, passwordField, confpassField;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_register1, container, false);

        emailField = view.findViewById(R.id.emailTextField);
        passwordField = view.findViewById(R.id.passwordTextField);
        confpassField = view.findViewById(R.id.confirmTextField);

        return view;
    }

    public EditText getEmailField() {
        return emailField;
    }

    public EditText getPasswordField() {
        return passwordField;
    }

    public EditText getConfpassField() {
        return confpassField;
    }
}
