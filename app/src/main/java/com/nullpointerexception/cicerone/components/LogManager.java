package com.nullpointerexception.cicerone.components;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 *      LogManager
 *
 *      Manages user account handling login with various access methods.
 *
 *      @author Luca
 */
public class LogManager
{
    /*
            Singleton declaration
     */
    private static final LogManager ourInstance = new LogManager();
    public static LogManager get() { return ourInstance; }
    private LogManager() {  }

    /** Request code for google sign-in intent */
    private final int GOOGLE_SIGNIN_REQUEST = 10;

    /*
            Vars
     */
    /** Stores instance of Firebase auth */
    private FirebaseAuth auth;
    /** Stores context, used for some actions of this class that requires it */
    private Context context;
    /** Current user logged in app */
    private User currentUser;
    /** Stores a LoginAttempt instance used when shared by more than one method */
    private LoginAttempt currentLoginAttempt;

    /**
     *      Initialize all fields required to use this class
     *      @param context Context that will be used by this class
     */
    public void initialize(Activity context)
    {
        this.context = context;
        FirebaseApp.initializeApp(context);
        auth = FirebaseAuth.getInstance();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount( context );
        if(account != null)
            currentUser = new User(account);
    }

    /**
     *      Tries to access to an account with specified credentials.
     *
     *      @param email    User's Email
     *      @param password User's Password
     *
     *      @return An instance of LoginAttempt with allows to add
     *      a callback method for this given attempt.
     */
    public LoginAttempt login(String email, String password)
    {
        final LoginAttempt loginAttempt = new LoginAttempt();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            //  Check if user exists and if it's verified
                            if(auth.getCurrentUser() != null && auth.getCurrentUser().isEmailVerified())
                            {
                                //  Set current user
                                currentUser = new User(auth.getCurrentUser());

                                // call the callback method, if set, with positive result
                                if(loginAttempt.getOnLoginResultListener() != null)
                                    loginAttempt.getOnLoginResultListener().onLoginResult(true);
                            }
                            else
                                if(loginAttempt.getOnLoginResultListener() != null)
                                    loginAttempt.getOnLoginResultListener().onLoginResult(false);
                        }
                        else
                        {
                            // call the callback method, if set, with negative result
                            if(loginAttempt.getOnLoginResultListener() != null)
                                loginAttempt.getOnLoginResultListener().onLoginResult(false);
                        }

                    }
                });

        return loginAttempt;
    }

    /**
     *      Show dialog of Google sign-in.
     *
     *      NOTE: Calling this method requires call also loginWithGoogle(...) in
     *      onActivityResult(...) of the activity passed with parameters
     *      or this method will do nothing.
     *
     *      @param activity Activity that will manage intent launched by this method.
     *
     *      @return         An instance of LoginAttempt with allows to add
     *                      a callback method for this given attempt.
     */
    public LoginAttempt requestLoginWithGoogle(Activity activity)
    {
        currentLoginAttempt = new LoginAttempt();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient;
        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, GOOGLE_SIGNIN_REQUEST);

        return currentLoginAttempt;
    }

    /**
     *      Manage result of intent launched with requestLoginWithGoogle(...) method:
     *      Set current user with data of google account which user has just signed-in.
     *
     *      NOTE: Call this method only if called also before requestLoginWithGoogle(...) and only
     *      in onActivityResult(...) of the activity where has been called the previous method.
     *
     *      @param requestCode  Request code provided by onActivityResult(...)
     *      @param data         Intent provided by onActivityResult(...)
     */
    public void loginWithGoogle(int requestCode, Intent data)
    {
        //  Check if got a null intent
        if(data == null)
            return;

        if(requestCode == GOOGLE_SIGNIN_REQUEST)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try
            {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if(account != null)
                {
                    // set current user
                    currentUser = new User(account);

                    // call the callback method, if set, with positive result
                    if(currentLoginAttempt != null && currentLoginAttempt.getOnLoginResultListener() != null)
                        currentLoginAttempt.getOnLoginResultListener().onLoginResult(true);

                    currentLoginAttempt = null;
                }
                else
                {
                    if (currentLoginAttempt != null && currentLoginAttempt.getOnLoginResultListener() != null)
                        currentLoginAttempt.getOnLoginResultListener().onLoginResult(false);

                    currentLoginAttempt = null;
                }
            }
            catch (ApiException e)
            {
                e.printStackTrace();

                if(currentLoginAttempt != null && currentLoginAttempt.getOnLoginResultListener() != null)
                    currentLoginAttempt.getOnLoginResultListener().onLoginResult(false);

                currentLoginAttempt = null;
            }
        }
    }

    /**
     *     @return User currently logged.
     */
    public User getUserLogged()
    {
        return currentUser;
    }

    /**
     *      User sign out
     */
    public void logout()
    {
        if(currentUser == null || auth == null || context == null)
            return;

        switch (currentUser.getAccessType())
        {
            case GOOGLE:
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();
                GoogleSignInClient mGoogleSignInClient;
                mGoogleSignInClient = GoogleSignIn.getClient(((Activity) context), gso);

                /*
                        NOTE: Google sign out is ASYNCHRONOUS!
                        If needed insert a callback method (there's one default of google)
                 */
                mGoogleSignInClient.signOut();
                break;

            case DEFAULT:
            default:
                auth.signOut();
        }
    }

    /**
     *      LoginAttempt
     *
     *      Class that allows to set a callback method for a login attempt.
     */
    public static class LoginAttempt
    {
        /** Interface with the mentioned callback method */
        public interface OnLoginResultListener
        {
            /**
             *      Callback method called after a login attempt.
             *      @param result true if user logged successfully, false else.
             */
            void onLoginResult(boolean result);
        }

        /** Stores the implementation provided */
        private OnLoginResultListener onLoginResultListener;

        /**
         *      Add an implementation for the method called after a login result.
         *      @param onLoginResultListener implementation to provide.
         * */
        public void addOnLoginResultListener(OnLoginResultListener onLoginResultListener)
        {
            this.onLoginResultListener = onLoginResultListener;
        }

        private OnLoginResultListener getOnLoginResultListener() { return onLoginResultListener; }
    }
}
