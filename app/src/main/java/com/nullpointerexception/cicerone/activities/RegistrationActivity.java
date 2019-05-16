package com.nullpointerexception.cicerone.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.fragments.Fragment_register1;
import com.nullpointerexception.cicerone.fragments.Fragment_register2;

public class RegistrationActivity extends AppCompatActivity
{


    EditText emailField, passwordField, confpassField, nameField, surnameField, date_birthField;
    Fragment fragment1, fragment2;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
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


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart()
    {
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

        if(view == findViewById(R.id.nextImageView))
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
        fragmentTransaction.remove( fragmentManager.getFragments()
                .get(fragmentManager.getFragments().size() -1 ));

        // Add new fragment
        fragmentTransaction.add(R.id.frameview, fragment);

        fragmentTransaction.commit();


    }




    /**
     *
     * @param view
     */
    public void startRegistration(View view) {

        if(checkFields())
            createFirebaseUser("ilmatty98s@gmail.com", "vitovito");

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
                            showDialog("Registrazione effettuata con successo!", "Successo", android.R.drawable.ic_dialog_info);

                            // It call the method for send the verification email
                            sendVerificationEmail();


                        } else {
                            // If sign in fails, display a message to the user.

                            showDialog("Errore nella registrazione!", "Errore", android.R.drawable.ic_dialog_alert);
                        }
                    }
                });
    }


    /**
     *
     * @param message
     * @param title
     * @param icon
     */
    private void showDialog(String message, String title, int icon) {

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //updateUI();
                    }
                })
                .setIcon(icon)
                .show();
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




    /*
    È quindi possibile controllare se l'utente abbia verificato la loro e-mail in qualsiasi punto della app chiamando:


    mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
          firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser != null ) {
                Log.e(TAG, firebaseUser.isEmailVerified() ? "User is signed in and email is verified" : "Email is not verified");
            } else {
                Log.e(TAG, "onAuthStateChanged:signed_out");
            }
        }
    };
     */




}
