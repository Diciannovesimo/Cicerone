package com.nullpointerexception.cicerone.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.fragments.Fragment_register1;
import com.nullpointerexception.cicerone.fragments.Fragment_register2;

public class RegistrationActivity extends AppCompatActivity {


    EditText emailField, passwordField, confpassField, nameField, surnameField, date_birthField;

    Fragment fragment1, fragment2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment_register1 fragment_register11 = new Fragment_register1();
        fragmentTransaction.add(R.id.frameview, fragment_register11);
        fragmentTransaction.commit();

        emailField = findViewById(R.id.emailTextField);
        passwordField = findViewById(R.id.passwordTextField);
        confpassField = findViewById(R.id.confirmTextField);
        nameField = findViewById(R.id.nameTextField);
        surnameField = findViewById(R.id.surnameTextField);
        date_birthField = findViewById(R.id.dateTextField);


    }

    /**
     *      It allows to switch the fragment of registration.
     */
    public void switchRegisterFragment(View view)
    {
        Fragment fragment;

        if(view == findViewById(R.id.nextImageView))
        {
            if(fragment1 == null)
                fragment1 = new Fragment_register2();
            fragment = fragment1;
        }
        else
        {
            if(fragment2 == null)
                fragment2 = new Fragment_register1();
            fragment = fragment2;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //  Remove last attached fragment
        fragmentTransaction.setCustomAnimations(R.anim.modal_in, R.anim.modal_out);
        fragmentTransaction.remove( fragmentManager.getFragments().get(
                        fragmentManager.getFragments().size() -1 ));

        // Add new fragment
        fragmentTransaction.add(R.id.frameview, fragment);

        fragmentTransaction.commit();

        //TODO: Nascodere il fragment di dietro


    }
}
