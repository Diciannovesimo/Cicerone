package com.nullpointerexception.cicerone.components;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

    /*
            Vars
     */
    private FirebaseAuth auth;
    private Context context;

    /**
     *      Initialize all fields required to use this class
     *      @param context Context that will be used by this class
     */
    public void initialize(Context context)
    {
        this.context = context;
        FirebaseApp.initializeApp(context);
        auth = FirebaseAuth.getInstance();
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
                            if(auth.getCurrentUser() != null && auth.getCurrentUser().isEmailVerified())
                                loginAttempt.getOnLoginResultListener().onLoginResult(true);
                            else
                                loginAttempt.getOnLoginResultListener().onLoginResult(false);
                        }
                        else
                        {
                            loginAttempt.getOnLoginResultListener().onLoginResult(false);
                        }

                    }
                });

        return loginAttempt;
    }

    /**
     *     @return User currently logged.
     */
    public FirebaseUser getUserLogged()
    {
        return auth.getCurrentUser();
    }

    /**
     *      User sign out
     */
    public void logout()
    {
        auth.signOut();
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
