package com.nullpointerexception.cicerone.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.components.Blocker;

import java.util.Calendar;


public class Fragment_register2 extends Fragment
{

    private EditText nameField, surnameField, date_birthField, phonePicker;

    //Datepicker object
    private Calendar calendar;
    private DatePickerDialog dpd;
    private String birthdayString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_register2, container, false);

        nameField = view.findViewById(R.id.nameTextField);
        surnameField = view.findViewById(R.id.surnameTextField);
        date_birthField = view.findViewById(R.id.dateTextField);
        phonePicker = view.findViewById(R.id.phoneTextField);

        date_birthField.setOnClickListener(new View.OnClickListener() {
            private Blocker mBlocker = new Blocker();

            @Override
            public void onClick(View v) {
                if (!mBlocker.block()) {
                    calendar = Calendar.getInstance();
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int month = calendar.get(Calendar.MONTH);
                    int year = calendar.get(Calendar.YEAR);

                    dpd = new DatePickerDialog(view.getContext(), R.style.DialogTheme, (view1, year1, month1, dayOfMonth) -> {
                        birthdayString = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                        date_birthField.setText(birthdayString);
                    }, year, month, day);
                    dpd.show();
                }
            }
        });

        return view;
    }

    public String getBirthdayString() {
        return birthdayString;
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

    public EditText getPhonePicker() { return phonePicker; }
}
