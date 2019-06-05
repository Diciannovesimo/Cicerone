package com.nullpointerexception.cicerone.components;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.nullpointerexception.cicerone.R;

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
    /**  Instance of this class   */
    private static final AuthenticationManager ourInstance = new AuthenticationManager();
    /**  Used to access to this class   */
    public static AuthenticationManager get() { return ourInstance; }
    /**  Private constructor to permit a single instance of this class   */
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
     *
     *      @param context Context that will be used by this class
     *
     *      @return null: if there aren't users logged, an instance of login attempt otherwise.
     */
    public LoginAttempt initialize(Activity context)
    {
        this.context = context;
        FirebaseApp.initializeApp(context);
        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null)
        {
            currentUser = new User(auth.getCurrentUser());

            BackEndInterface.get().getEntity(currentUser, new BackEndInterface.OnOperationCompleteListener()
            {
                @Override
                public void onSuccess()
                {
                    if(currentLoginAttempt != null && currentLoginAttempt.getOnLoginResultListener() != null)
                        currentLoginAttempt.getOnLoginResultListener().onLoginResult(true);
                }

                @Override
                public void onError()
                {
                    if(currentLoginAttempt != null && currentLoginAttempt.getOnLoginResultListener() != null)
                        currentLoginAttempt.getOnLoginResultListener().onLoginResult(false);
                }
            });
        }
        else
        {
            return null;
        }

        currentLoginAttempt = new LoginAttempt();
        return currentLoginAttempt;
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
            logout();

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
     *      Tries to get Uid of an account with specified credentials.
     *
     *      @param email    User's Email
     *      @param password User's Password
     *
     *      @return An instance of LoginAttempt with allows to add
     *      a callback method for this given attempt.
     */
    public LoginAttempt getUIdOf(String email, String password)
    {
        final LoginAttempt loginAttempt = new LoginAttempt();

        //  Don't allow to login if before signed out with current account.
        if(currentUser != null)
            logout();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            //  Check if user exists and if it's verified
                            if(auth.getCurrentUser() != null)
                            {
                                String uid = auth.getUid();

                                //  Call the callback method, if set, with positive result
                                if(loginAttempt.getOnUidListener() != null)
                                    loginAttempt.getOnUidListener().onIdObtained(uid);
                            }
                            else
                                //  Call the callback method, if set, with negative result
                                if(loginAttempt.getOnUidListener() != null)
                                    loginAttempt.getOnUidListener().onError();
                        }
                        else
                        {
                            //  Call the callback method, if set, with negative result
                            if(loginAttempt.getOnUidListener() != null)
                                loginAttempt.getOnUidListener().onError();
                        }

                    }
                });

        return loginAttempt;
    }

    /**
     *      Tries to delete an account sending requests until work is done successfully.
     *
     *      @param email        Email of account to delete
     *      @param password     Password of account to delete
     */
    public void deleteUser(final String email, final String password)
    {
        if(email == null || password == null)
            return;

        if(auth.getCurrentUser() != null)
        {
            if(auth.getCurrentUser().getEmail() == null)
                return;
            else if(auth.getCurrentUser().getEmail().equals(email))
            {
                auth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if( ! task.isSuccessful())
                        {
                            deleteUser(email, password);
                        }
                        else
                            logout();
                    }
                });
            }
            else
            {
                logout();
                deleteUser(email, password);
            }
        }
        else
            login(email, password).addOnLoginResultListener(new LoginAttempt.OnLoginResultListener()
            {
                @Override
                public void onLoginResult(boolean result)
                {
                    deleteUser(email, password);
                }
            });
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
                .requestIdToken(context.getString(R.string.default_web_client_id))
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
                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    auth.signInWithCredential(credential)
                            .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        if(auth.getCurrentUser() != null)
                                            currentUser = new User(auth.getCurrentUser());

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
                            });


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
                Log.e("Error", e.toString());

                if(currentLoginAttempt != null && currentLoginAttempt.getOnLoginResultListener() != null)
                    currentLoginAttempt.getOnLoginResultListener().onLoginResult(false);

                currentLoginAttempt = null;
            }
        }
    }

    /**
     *      Sign in with facebook account
     *
     *      @param loginResult  Result of Login Sign-in callback success method
     */
    public LoginAttempt setFacebookUser(LoginResult loginResult)
    {
        AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            if(auth.getCurrentUser() != null)
                            {
                                currentUser = new User(auth.getCurrentUser());

                                if(currentLoginAttempt != null && currentLoginAttempt.getOnLoginResultListener() != null)
                                    currentLoginAttempt.getOnLoginResultListener().onLoginResult(true);
                            }
                            else
                            {
                                if(currentLoginAttempt != null && currentLoginAttempt.getOnLoginResultListener() != null)
                                    currentLoginAttempt.getOnLoginResultListener().onLoginResult(false);
                            }
                        }
                        else
                        {
                            if(currentLoginAttempt != null && currentLoginAttempt.getOnLoginResultListener() != null)
                                currentLoginAttempt.getOnLoginResultListener().onLoginResult(true);
                        }
                    }
                });

        currentLoginAttempt = new LoginAttempt();
        return currentLoginAttempt;
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
    }

    /**
     *     @return User currently logged.
     */
    public User getUserLogged()
    {
        return currentUser;
    }

    /**
     *      @return true: if user is logged, false otherwise
     */
    public boolean isUserLogged()
    {
        if(auth == null)
            return false;

        return auth.getCurrentUser() != null;
    }

    /**
     *      User sign out
     */
    public void logout()
    {
        auth.signOut();
        currentUser = null;
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
     *      Class that allows to set callback methods for a login attempt.
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

        /** Interface with the mentioned callback method */
        public interface OnUIDListener
        {
            /**
             *      Callback method called after a login attempt.
             *      @param uid User Id provided by FireBase
             */
            void onIdObtained(String uid);

            /**     Callback method called after a failed login attempt. */
            void onError();
        }

        /** Stores the implementation provided of OnLoginResultListener */
        private OnLoginResultListener onLoginResultListener;

        /** Stores the implementation provided of OnUIDListener */
        private OnUIDListener onUidListener;

        /**
         *      Add an implementation for the method called after a login result.
         *      @param onLoginResultListener implementation to provide.
         * */
        public void addOnLoginResultListener(OnLoginResultListener onLoginResultListener)
        {
            this.onLoginResultListener = onLoginResultListener;
        }

        public OnLoginResultListener getOnLoginResultListener() { return onLoginResultListener; }

        /**
         *      Add an implementation for the method called after a login result.
         *      @param onUidListener implementation to provide.
         * */
        public void addOnUidListener(OnUIDListener onUidListener)
        {
            this.onUidListener = onUidListener;
        }

        public OnUIDListener getOnUidListener() { return onUidListener; }

    }
}