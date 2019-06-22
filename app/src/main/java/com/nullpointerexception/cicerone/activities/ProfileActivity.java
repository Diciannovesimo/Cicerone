package com.nullpointerexception.cicerone.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kinda.mtextfield.ExtendedEditText;
import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.components.BackEndInterface;
import com.nullpointerexception.cicerone.components.ProfileImageFetcher;
import com.nullpointerexception.cicerone.components.User;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private TextView mEmail, mTelephone, mDate, mName, mItinerariesAsParticipant;
    private ExtendedEditText mComment;
    private ImageView profileImage;
    private RatingBar ratingBar;
    private Button sndFeedBtn;
    private float rating;
    private User user;
    private RecyclerView recyclerView;

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


        recyclerView = findViewById(R.id.RecyclerView_Review);

        /**
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        AdapterPartecipants adapter = new AdapterPartecipants(getApplicationContext(), itinerary.getParticipants());
        recyclerView.setAdapter(adapter);
         **/
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
/**
class AdapterReview extends RecyclerView.Adapter <AdapterPartecipants.MyViewHolder>
{
    private Context context;
    private List<User> participants;
    private LayoutInflater inflater;

    public AdapterReview(Context appContext,List<User> participants)
    {
        this.context = appContext;
        this.participants = participants;
        inflater = (LayoutInflater.from(appContext));
    }

    @NonNull
    @Override
    public AdapterPartecipants.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = inflater.inflate(R.layout.participant_layout, parent, false);
        AdapterPartecipants.MyViewHolder vh = new AdapterPartecipants.MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPartecipants.MyViewHolder holder, int position) {
        holder.name.setText(participants.get(position).getName());
        holder.surname.setText(participants.get(position).getSurname());


        new ProfileImageFetcher(context).fetchImageOf(participants.get(position), drawable ->
        {

            if(drawable != null)
                holder.imgProfile.setImageDrawable(drawable);
        });


        //Ascolto l'evento click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,participants.get(position).getName() + participants.get(position).getSurname(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name, surname;
        ImageView imgProfile;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.textView_Name);
            surname = (TextView) itemView.findViewById(R.id.textView_Surname);
            imgProfile = (ImageView) itemView.findViewById(R.id.imageView_ProfileImage);

        }
    }
}
**/