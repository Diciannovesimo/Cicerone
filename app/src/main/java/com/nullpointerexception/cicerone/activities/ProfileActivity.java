package com.nullpointerexception.cicerone.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kinda.mtextfield.ExtendedEditText;
import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.components.BackEndInterface;
import com.nullpointerexception.cicerone.components.ProfileImageFetcher;
import com.nullpointerexception.cicerone.components.User;

public class ProfileActivity extends AppCompatActivity {

    private TextView mEmail, mTelephone, mDate, mName, mItinerariesAsParticipant;
    private ExtendedEditText mComment;
    private ImageView profileImage;
    private RatingBar ratingBar;
    private Button sndFeedBtn;
    private float rating;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window w = getWindow();
        w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Initialize UI
        initUI();

        //Change color of toolbar
        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#FF5500"));

        Intent intent = getIntent();
        String extra = intent.getExtras().getString("id_cicerone_to_show");

        user = new User();
        user.setId(extra);

        BackEndInterface.get().getEntity(user, new BackEndInterface.OnOperationCompleteListener() {
            @Override
            public void onSuccess() {
                if(user.getId().isEmpty() && user.getDisplayName().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Impossibile caricare le" +
                            "informazioni di profilo", Toast.LENGTH_SHORT);
                    finish();
                }else {
                    //Set text in the field
                    setTextField();
                }
            }

            @Override
            public void onError() {

            }
        });
    }

    public void initUI() {
        mEmail = findViewById(R.id.profileMail_tv);
        mTelephone = findViewById(R.id.profilePhone_tv);
        mDate = findViewById(R.id.profileDate_tv);
        mName = findViewById(R.id.profileName_tv);
        mItinerariesAsParticipant = findViewById(R.id.itinerariesAsParticipant_tv);
        profileImage = findViewById(R.id.us_image);
        ratingBar = findViewById(R.id.ratingBar);
        mComment = findViewById(R.id.commento_tv);
        sndFeedBtn = findViewById(R.id.sndFeed_btn);
    }

    /**
     * @brief Set text field
     */
    public void setTextField() {

        new ProfileImageFetcher(this)
                .fetchImageOf(user, drawable -> {
                    profileImage.setImageDrawable(drawable);
                });

        //Set name field
        mName.setText(user.getDisplayName());

        //set email
        if(user.getEmail() != null) {
            mEmail.setText(user.getEmail());
        }

        //set dateBirth
        if(user.getDateBirth() != null) {
            mDate.setText(user.getDateBirth());
        }

        //Set the number of itinerary in which the user participated
        mItinerariesAsParticipant.setText(String.valueOf(user.getItineraries().size()));

        //Set telephone number
        mTelephone.setText(user.getPhoneNumber());

        //Set ratingBar listener
        ratingBarListener();

        sndFeedBtn.setOnClickListener(v -> {
            if(mComment != null && !mComment.getText().toString().isEmpty()) {
                //setta il commento in ueser
            } else if(rating != 0) {
                //setta il rating in user
                //setta lo storeEntity
            }
        });
    }

    private void ratingBarListener() {

        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            this.rating = rating;
        });

    }
}
