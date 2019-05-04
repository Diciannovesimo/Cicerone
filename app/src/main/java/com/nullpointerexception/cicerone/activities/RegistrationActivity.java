package com.nullpointerexception.cicerone.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kinda.alert.KAlertDialog;
import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.fragments.Fragment_register1;
import com.nullpointerexception.cicerone.fragments.Fragment_register2;

import java.util.Calendar;

public class RegistrationActivity extends AppCompatActivity {

    //Universal tag for RegistrationActivity
    final static String TAG = "registration_activity";

    private Fragment_register1 fragment1;
    private Fragment_register2 fragment2;
    private LottieAnimationView animationView;


    //Firebase object
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment1 = new Fragment_register1();
        fragmentTransaction.add(R.id.frameview, fragment1);
        fragmentTransaction.commit();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    /**
     *      It allows to switch the fragment of registration.
     *      @param view
     */
    public void switchRegisterFragment(View view)
    {
        Fragment fragment;

        if(view == findViewById(R.id.animation_next_btn))
        {

            if(fragment2 == null)
                fragment2 = new Fragment_register2();
            fragment = fragment2;

        }
        else
        {
            if(fragment1 == null)
                fragment1 = new Fragment_register1();
            fragment = fragment1;

        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //  Remove last attached fragment
        fragmentTransaction.setCustomAnimations(R.anim.modal_in, R.anim.modal_out);

        //  Check if the number of fragment is 2 after firebase registration error
        if(fragmentManager.getFragments().size() == 2) {

            for (Fragment fragment_index:getSupportFragmentManager().getFragments()) {
                getSupportFragmentManager().beginTransaction().remove(fragment_index).commit();
            }

        }else {
            fragmentTransaction.remove(fragmentManager.getFragments()
                    .get(fragmentManager.getFragments().size() - 1));
        }

        // Add new fragment
        fragmentTransaction.add(R.id.frameview, fragment);
        fragmentTransaction.commit();

    }

    /**
     *      Check if fields are correctly inserted by user,
     *      provides errors else.
     */
    private boolean checkFields()
    {
        boolean alright = true;

        return alright;
    }

    /**
     *
     * @param email
     * @param password
     */
    private void createFirebaseUser(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            showDialog(true);

                            // It call the method for send the verification email
                            sendVerificationEmail();


                        } else {
                            // If sign in fails, display a message to the user.
                            showDialog(false);
                        }
                    }
                });
    }

    /**
     *
     * @param result
     */
    private void showDialog(Boolean result) {

        if(!result) {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    // Show error message
                    new KAlertDialog(RegistrationActivity.this, KAlertDialog.ERROR_TYPE)
                            .setTitleText(getResources().getString(R.string.registrationDialogErrorText1))
                            .setContentText(getResources().getString(R.string.registrationDialogErrorText2))
                            .setConfirmText("OK")
                            .show();
                }
            });
        }else {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    // Show error message
                    new KAlertDialog(RegistrationActivity.this, KAlertDialog.SUCCESS_TYPE)
                            .setTitleText(getResources().getString(R.string.registrationDialogSuccesText1))
                            .setConfirmText("OK")
                            .show();
                }
            });
        }
    }

    /**
     *
     */
    public void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //Toast.makeText(SignUpActivity.this, "Signup successful. Verification email sent", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    public void startRegistration(View view) {


        animationView = findViewById(R.id.animation_confirm_btn);
        animationView.setSpeed(1.5f);
        animationView.playAnimation();

        if(checkFields())
            createFirebaseUser("ilmatty98s@gmail.com", "vitovito");

    }

}
