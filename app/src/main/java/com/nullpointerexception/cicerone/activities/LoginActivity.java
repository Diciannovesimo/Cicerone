package com.nullpointerexception.cicerone.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.kinda.alert.KAlertDialog;
import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.components.AuthenticationManager;
import com.nullpointerexception.cicerone.components.BackEndInterface;

import java.util.Timer;
import java.util.TimerTask;

/**
 *      LoginActivity
 *
 *      Activity where user can log in with his account.
 *
 *      @author Luca
 */
public class LoginActivity extends AppCompatActivity
{

    /*
            Views
     */
    private View loginButton, googleSignInButton, facebookSignInButton;
    private HorizontalScrollView imageScroller;
    private EditText emailField, passwordField;
    private TextView registrationButton;
    private CallbackManager callbackManager;

    //  Developer tools
    private int touched = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*
                Assignments
         */
        imageScroller = findViewById(R.id.imageScroller);
        emailField = findViewById(R.id.emailTextField);
        passwordField = findViewById(R.id.confirmTextField);
        loginButton = findViewById(R.id.loginButton);
        registrationButton = findViewById(R.id.registerButton);
        googleSignInButton = findViewById(R.id.googleSignInButton);
        facebookSignInButton = findViewById(R.id.facebookSignInButton);

        //  Developer tools enabling
        findViewById(R.id.bannerLogo).setOnClickListener(view ->
        {
            if(touched == 0)
                new Handler().postDelayed(() -> touched = 0, 1000);

            if(touched == 5)
            {
                EditText editText = new EditText(this);
                editText.setHint("Code");
                new AlertDialog.Builder(this)
                        .setTitle("Developer mode")
                        .setView(editText)
                        .setPositiveButton("Access", (dialogInterface, i) ->
                        {
                            String text = editText.getText().toString();

                            if(text.equalsIgnoreCase("corvo di bitritto"))
                            {
                                SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);

                                if(sharedPreferences.getBoolean("developer_enabled", false))
                                {
                                    sharedPreferences.edit().putBoolean("developer_enabled", false).apply();
                                    Toast.makeText(this, "Modalità sviluppatore disattivata.", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    sharedPreferences.edit().putBoolean("developer_enabled", true).apply();
                                    Toast.makeText(this, "Ora sei in modalità sviluppatore.", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                                Toast.makeText(this, "Codice errato", Toast.LENGTH_SHORT).show();

                            if(text.equalsIgnoreCase("william"))
                                Toast.makeText(this, "assertNotNull(everything);", Toast.LENGTH_SHORT).show();

                            if(text.equalsIgnoreCase("claudio"))
                                Toast.makeText(this, "Git master", Toast.LENGTH_SHORT).show();

                            if(text.equalsIgnoreCase("mattia"))
                                Toast.makeText(this, "Showdown master", Toast.LENGTH_SHORT).show();

                            if(text.equalsIgnoreCase("luca"))
                                Toast.makeText(this, "programmatore android java esperto", Toast.LENGTH_SHORT).show();

                        })
                        .setCancelable(false)
                        .create().show();
            }
            else
                touched++;
        });

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().setAuthType("rerequest");
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>()
                {
                    @Override
                    public void onSuccess(LoginResult loginResult)
                    {
                        AuthenticationManager.get().setFacebookUser(loginResult)
                            .addOnLoginResultListener(new AuthenticationManager.LoginAttempt.OnLoginResultListener()
                            {
                                @Override
                                public void onLoginResult(boolean result)
                                {
                                    if(result)
                                    {
                                        BackEndInterface.get().getEntity(AuthenticationManager.get().getUserLogged(),
                                        new BackEndInterface.OnOperationCompleteListener()
                                        {
                                            @Override
                                            public void onSuccess()
                                            {
                                                BackEndInterface.get().storeEntity( AuthenticationManager.get().getUserLogged() );

                                                LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                finish();
                                            }

                                            @Override
                                            public void onError()
                                            {
                                                runOnUiThread(new Runnable()
                                                {
                                                    @Override
                                                    public void run()
                                                    {
                                                        Toast.makeText(LoginActivity.this,
                                                                getResources().getString(R.string.generic_error),
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                    else
                                    {

                                    }
                                }
                            });
                    }

                    @Override
                    public void onCancel()
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(LoginActivity.this,
                                        getResources().getString(R.string.generic_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(FacebookException exception)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(LoginActivity.this,
                                        getResources().getString(R.string.generic_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

        /*
                Initialization
         */

        //  Prevent user to scroll background image
        imageScroller.setOnTouchListener((view, motionEvent) -> true);

        //  Make registration TextView underlined
        SpannableString content = new SpannableString(getResources().getString(R.string.loginText2));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        registrationButton.setText(content);

        //  Set color variation on user click
        registrationButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {

                switch (motionEvent.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        registrationButton.setTextColor( getResources().getColor(R.color.colorAccent2) );
                        break;

                    case MotionEvent.ACTION_UP:
                        registrationButton.setTextColor( getResources().getColor(R.color.colorAccent) );
                        registrationButton.performClick();
                        break;
                }

                return true;
            }
        });

        /*
              Add bottom margin to UI controls to prevent them to be hidden from system UI.
         */
        //  Get height of system navigation UI
        int bottomNavigationBarHeight = 0;
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0)
            bottomNavigationBarHeight = getResources().getDimensionPixelSize(resourceId);
        LinearLayout controlsContainer = findViewById(R.id.controlsContainer);
        // Get views rect
        Rect controlsRect = new Rect();
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        Rect systemNavigationRect = new Rect(0, screenHeight - bottomNavigationBarHeight,
                0, screenHeight);
        controlsContainer.getDrawingRect(controlsRect);
        // Check if there's an intersection
        if(systemNavigationRect.intersect(controlsRect))
        {
            //  Add bottom margin
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) controlsContainer.getLayoutParams();
            params.bottomMargin += bottomNavigationBarHeight;
            controlsContainer.setLayoutParams(params);
        }

        /*
                Set interaction events
          */

        registrationButton.setOnClickListener(view ->
        {
            Intent register_activity = new Intent(getApplicationContext(), RegistrationActivity.class);
            startActivity(register_activity);
        });

        loginButton.setOnClickListener(view -> checkFields());

        googleSignInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AuthenticationManager.get().requestLoginWithGoogle(LoginActivity.this)
                        .addOnLoginResultListener(new AuthenticationManager.LoginAttempt.OnLoginResultListener()
                        {
                            @Override
                            public void onLoginResult(boolean result)
                            {
                                if(result)  //  Login successful
                                {
                                    BackEndInterface.get().getEntity(AuthenticationManager.get().getUserLogged(),
                                            new BackEndInterface.OnOperationCompleteListener()
                                            {
                                                @Override
                                                public void onSuccess()
                                                {
                                                    BackEndInterface.get().storeEntity( AuthenticationManager.get().getUserLogged() );

                                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                    finish();
                                                }

                                                @Override
                                                public void onError()
                                                {
                                                    runOnUiThread(new Runnable()
                                                    {
                                                        @Override
                                                        public void run()
                                                        {
                                                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.generic_error),
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });
                                }
                            }
                        });
            }
        });

        facebookSignInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AuthenticationManager.get().loginWithFacebook(LoginActivity.this);
            }
        });
    }

    /** Regular expression to validate email addresses. */
    private final String EMAIL_REGEX = "(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*:(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)(?:,\\s*(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*))*)?;\\s*)";

    /**
     *      Check if fields are correctly inserted by user,
     *      provides errors else.
     */
    private void checkFields()
    {
        boolean alright = true;

        //  Check email
        if( ! emailField.getText().toString().trim().matches(EMAIL_REGEX))
        {
            alright = false;
            emailField.setError( getResources().getString(R.string.emailTextFieldError) );
        }

        // Check password
        if(passwordField.getText().toString().trim().length() < 8)
        {
            alright = false;
            passwordField.setError( getResources().getString(R.string.passwordTextFieldError) );
        }

        if(alright)
            login();
    }

    /** Tag of a view with an animation displayed while logging in. */
    private final String LOGIN_ANIMATION_TAG = "com.nullpointerexception.cicerone.LoginAnimation";
    /** Limit time to try to access with user credentials, expressed in milliseconds. */
    private final int LOGIN_TIMEOUT = 30 * 1000;    // 30 seconds
    /** Timer that avoids login attempt after a given delay. */
    private Timer loginTimer;

    /**
     *      Tries to access to the account with credentials inserted by user.
     */
    @SuppressLint("ClickableViewAccessibility")
    private void login()
    {

        /*
                Add graphic loading animation
         */
        ViewGroup root = (ViewGroup) passwordField.getRootView();
        FrameLayout container = new FrameLayout(this);
        container.setTag(LOGIN_ANIMATION_TAG);
        container.setBackgroundColor(Color.parseColor("#77000000"));
        LottieAnimationView animationView = new LottieAnimationView(this);
        animationView.setAnimation(R.raw.loading);
        animationView.loop(true);
        int size = getResources().getDisplayMetrics().widthPixels / 4;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
        params.gravity = Gravity.CENTER;
        animationView.setLayoutParams(params);
        animationView.playAnimation();
        container.addView(animationView);
        //  Disables interactions with others views under this
        container.setOnTouchListener(new View.OnTouchListener()
        { public boolean onTouch(View view, MotionEvent motionEvent) { return true; }});
        root.addView(container);

        /*
                Login attempt
         */
        AuthenticationManager.get().login(emailField.getText().toString(), passwordField.getText().toString())
                .addOnLoginResultListener(result ->
                {
                    /*
                            Removes login animation
                     */
                    runOnUiThread(() ->
                    {
                        ViewGroup root1 = (ViewGroup) passwordField.getRootView();
                        View target = root1.findViewWithTag(LOGIN_ANIMATION_TAG);
                        if(target != null)
                            root1.removeView(target);
                    });

                    loginTimer.cancel();

                    if(result)  //  Login successful
                    {
                        BackEndInterface.get().getEntity(AuthenticationManager.get().getUserLogged(),
                                new BackEndInterface.OnOperationCompleteListener()
                                {
                                    @Override
                                    public void onSuccess()
                                    {
                                        BackEndInterface.get().storeEntity( AuthenticationManager.get().getUserLogged() );

                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    }

                                    @Override
                                    public void onError()
                                    {
                                        LoginActivity.this.runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                Toast.makeText(getApplicationContext(),
                                                        getApplicationContext().getResources().getString(R.string.generic_error),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                    }
                    else        // Login failed
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                // Show error message
                                new KAlertDialog(LoginActivity.this, KAlertDialog.ERROR_TYPE)
                                        .setTitleText(getResources().getString(R.string.loginDialogText1))
                                        .setContentText(getResources().getString(R.string.loginDialogText2))
                                        .setConfirmText("OK")
                                        .show();
                            }
                        });
                    }
                });

        /*
                Reset and start timer
         */
        loginTimer = new Timer();
        loginTimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //  Remove login animation
                        ViewGroup root = (ViewGroup) passwordField.getRootView();
                        View target = root.findViewWithTag(LOGIN_ANIMATION_TAG);
                        if(target != null)
                            root.removeView(target);
                    }
                });
            }
        }, LOGIN_TIMEOUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        AuthenticationManager.get().loginWithGoogle(requestCode, data);
    }
}