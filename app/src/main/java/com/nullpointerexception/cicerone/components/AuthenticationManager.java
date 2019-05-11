package com.nullpointerexception.cicerone.components;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
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

import java.util.Arrays;

/**
 *      AuthenticationManager
 *
 *      Manages user account handling login with various access methods.
 *
 *      @author Luca
 */
public class AuthenticationManager
{
    /*
            Singleton declaration
     */
    private static final AuthenticationManager ourInstance = new AuthenticationManager();
    public static AuthenticationManager get() { return ourInstance; }
    private AuthenticationManager() {  }

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

        /*
                NOTE: The problem of multiple accounts logged will be resolved when will be
                implemented the local storage of user in shared prefs.
         */

        // Check if already logged with Google
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount( context );
        if(account != null)
            currentUser = new User(account);

        //  Check if already logged with facebook
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken != null)
        {
            LoginManager.getInstance().logInWithReadPermissions(context, Arrays.asList("public_profile"));
            boolean isLoggedIn = !accessToken.isExpired();
            if (isLoggedIn)
                if (Profile.getCurrentProfile() != null)
                    currentUser = new User(Profile.getCurrentProfile());
        }

        /*
        if(currentUser != null)
            BackEndInterface.get().storeField(BackEndInterface.Entities.user,
                    BackEndInterface.EntityFields.user_displayName,
                    currentUser.getDisplayName());
        else
            Log.i("TEST", "currentUser == null.");*/

        /*
        BackEndInterface.get().getField(BackEndInterface.Entities.user,
                BackEndInterface.EntityFields.user_displayName,
                new BackEndInterface.OnDataReceiveListener()
                {
                    @Override
                    public void onDataReceived(String data)
                    {
                        Log.i("TEST", "Received: " + data);
                    }

                    @Override
                    public void onError()
                    {
                        Log.i("TEST", "Error.");
                    }
                });*/

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

        //  Don't allow to login if before signed out with current account.
        if(currentUser != null)
            return loginAttempt;        // TODO: Question to user if want to sign out?

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

                                //  Call the callback method, if set, with positive result
                                if(loginAttempt.getOnLoginResultListener() != null)
                                    loginAttempt.getOnLoginResultListener().onLoginResult(true);
                            }
                            else
                                if(loginAttempt.getOnLoginResultListener() != null)
                                    loginAttempt.getOnLoginResultListener().onLoginResult(false);
                        }
                        else
                        {
                            // Call the callback method, if set, with negative result
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
                    // Set current user
                    currentUser = new User(account);

                    // Call the callback method, if set, with positive result
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
     *      Set current user as the profile passed with parameters.
     *
     *      @param profile Account to be set as current user
     */
    public void setFacebookUser(Profile profile)
    {
        if(profile != null)
            currentUser = new User(Profile.getCurrentProfile());
    }

    /**
     *      Sign in with a facebook account.
     *      It requires some actions made before as written on facebook login documentation.
     *
     *      @param activity Activity where implemented facebook sign in operations.
     */
    public void loginWithFacebook(Activity activity)
    {
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile"));
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if(isLoggedIn)
            if(Profile.getCurrentProfile() != null)
                currentUser = new User(Profile.getCurrentProfile());
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
            case FACEBOOK:
                LoginManager.getInstance().logOut();
                break;

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
     *  Create a new createAccount method which takes in an email address and password,
     *  validates them and then creates a new user
     *
     *  @param email of the user who has registered
     *  @param password of the user who has registered
     *  @param onCompleteListener Callback method to manage actions for different results
     */
    public void createFirebaseUser(String email, String password, OnCompleteListener<AuthResult> onCompleteListener)
    {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, onCompleteListener);
    }

    /**
     *     Send a registration confirmation email
     *
     *     @param onCompleteListener Callback method to manage actions for different results
     */
    public void sendVerificationEmail(OnCompleteListener<Void> onCompleteListener)
    {
        if (auth.getCurrentUser() != null)
            auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(onCompleteListener);
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
