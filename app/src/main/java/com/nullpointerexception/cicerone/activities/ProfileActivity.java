package com.nullpointerexception.cicerone.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kinda.mtextfield.ExtendedEditText;
import com.kinda.mtextfield.TextFieldBoxes;
import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.components.AuthenticationManager;
import com.nullpointerexception.cicerone.components.BackEndInterface;
import com.nullpointerexception.cicerone.components.Blocker;
import com.nullpointerexception.cicerone.components.Feedback;
import com.nullpointerexception.cicerone.components.ObjectSharer;
import com.nullpointerexception.cicerone.components.ProfileImageFetcher;
import com.nullpointerexception.cicerone.components.User;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity
{
    private static final String TAG = "ProfileActivity_log";
    private TextView mEmail, mTelephone, mDate, mName, sndFeedBtn, removeFeedback, feedbackTitle, goFeedBacksList;
    private ExtendedEditText mComment;
    private TextFieldBoxes textFieldBoxes;
    private ImageView profileImage;
    private RatingBar ratingBar, ratingBarAVG;
    private float rating;
    private User user;

    private int position = 0;
    private boolean found = false;

    private RecyclerView recyclerView;
    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window w = getWindow();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Initialize UI
        initUI();

        //Change color of toolbar
        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#FF5500"));
        user = new User();

        Intent intent = getIntent();
        String extra = "";

        intent.getExtras().getString("id_cicerone_to_show");
        extra = intent.getExtras().getString("id_cicerone_to_show");

        user.setId(extra);

        if(intent.getExtras().getString("id_cicerone_to_show") != null) {
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

        goFeedBacksList.setOnClickListener(new View.OnClickListener() {
            private Blocker mBlocker = new Blocker();

            @Override
            public void onClick(View v) {
                if (!mBlocker.block()) {
                    Intent intent2 = new Intent(v.getContext(), FeedBacksActivity.class);
                    ObjectSharer.get().shareObject("feedback", user);
                    startActivity(intent2);
                }
            }
        });

    }

    public void initUI() {
        mEmail = findViewById(R.id.profileMail_tv);
        mTelephone = findViewById(R.id.profilePhone_tv);
        mDate = findViewById(R.id.profileDate_tv);
        mName = findViewById(R.id.profileName_tv);
        profileImage = findViewById(R.id.us_image);
        ratingBar = findViewById(R.id.ratingBar);
        ratingBarAVG = findViewById(R.id.ratingBarAVG);
        mComment = findViewById(R.id.commento_tv);
        sndFeedBtn = findViewById(R.id.sndFeed_btn);
        constraintLayout = findViewById(R.id.feedback_layout);
        removeFeedback = findViewById(R.id.deleteFeedback_tv);
        feedbackTitle = findViewById(R.id.feedbackTitle_tv);
        goFeedBacksList = findViewById(R.id.goFeedBacksList);
        textFieldBoxes = findViewById(R.id.feedback_box);
    }

    /**
     * @brief Set text field
     */
    public void setTextField() {

        User userLogged = AuthenticationManager.get().getUserLogged();
        Feedback feedback = new Feedback(userLogged);

        ratingBarAVG.setRating(user.getAverageFeedback());


        new ProfileImageFetcher(this)
                .fetchImageOf(user, drawable -> {
                    profileImage.setImageDrawable(drawable);
                });

        found = false;
        position = 0;

        for(int i = 0; i < user.getFeedbacks().size(); ++i) {
            if(userLogged.getId().equals(user.getFeedbacks().get(i).getIdUser())) {
                mComment.setText(user.getFeedbacks().get(i).getComment());
                ratingBar.setRating((float) user.getFeedbacks().get(i).getVote());
                feedbackTitle.setText("Il tuo feedback");
                sndFeedBtn.setText("Modifica");
                position = i;
                found = true;
                break;
            }
        }
        //found is true if the user has already a feedback
        if(!found)
            removeFeedback.setEnabled(false);
        else
        if(user.getFeedbacks().get(position).getComment().isEmpty())
            textFieldBoxes.setVisibility(View.GONE);

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

        //Set telephone number
        mTelephone.setText(user.getPhoneNumber());


        //Set ratingBar listener
        if(!user.getId().equals(userLogged.getId())) {
            ratingBarListener();

            sndFeedBtn.setOnClickListener(new View.OnClickListener() {
                private Blocker mBlocker = new Blocker();

                @Override
                public void onClick(View v) {
                    if(!mBlocker.block()) {
                        for(int i = 0; i < user.getFeedbacks().size(); ++i) {
                            if (userLogged.getId().equals(user.getFeedbacks().get(i).getIdUser())) {
                                found = true;
                                position = i;
                            }
                        }

                        rating = (int) ratingBar.getRating();

                        if (found) {
                            if(checkError()) {
                                feedback.setComment(mComment.getText().toString());

                                if (rating != 0 && (rating != user.getFeedbacks().get(position).getVote() ||
                                        !feedback.getComment().equals(user.getFeedbacks().get(position).getComment()))) {
                                    feedback.setVote((int) rating);
                                    user.editFeedback(feedback);

                                    BackEndInterface.get().storeEntity(user,
                                            new BackEndInterface.OnOperationCompleteListener() {
                                                @Override
                                                public void onSuccess() {
                                                    Toast.makeText(v.getContext(), "Hai inserito il feedback", Toast.LENGTH_SHORT).show();
                                                    removeFeedback.setEnabled(true);
                                                    ratingBarAVG.setRating(user.getAverageFeedback());
                                                }

                                                @Override
                                                public void onError() {

                                                }
                                            });
                                } else{
                                    Toast.makeText(getApplicationContext(), "Hai inserito lo stesso voto", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            if(mComment != null && !mComment.getText().toString().isEmpty()) {
                                feedback.setComment(mComment.getText().toString());
                            }
                            if (rating != 0) {
                                feedback.setVote((int) rating);
                                user.addFeedback(feedback);

                                BackEndInterface.get().storeEntity(user,
                                        new BackEndInterface.OnOperationCompleteListener() {
                                            @Override
                                            public void onSuccess() {
                                                Toast.makeText(v.getContext(), "Hai inserito il feedback", Toast.LENGTH_SHORT).show();
                                                feedbackTitle.setText("Il tuo feedback");
                                                removeFeedback.setEnabled(true);
                                                sndFeedBtn.setText("Modifica");
                                                ratingBarAVG.setRating(user.getAverageFeedback());
                                            }

                                            @Override
                                            public void onError() {

                                            }
                                        });
                            }
                        }
                    }
                }
            });

            removeFeedback.setOnClickListener(new View.OnClickListener() {
                private Blocker mBlocker = new Blocker();

                @Override
                public void onClick(View v) {
                    if(!mBlocker.block()) {
                        for(int i = 0; i < user.getFeedbacks().size(); ++i) {
                            if (userLogged.getId().equals(user.getFeedbacks().get(i).getIdUser()))
                                position = i;
                        }
                        Feedback temp_feedback = user.getFeedbacks().get(position);
                        user.removeFeedback(temp_feedback);

                        BackEndInterface.get().removeEntity(user,
                                new BackEndInterface.OnOperationCompleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        BackEndInterface.get().storeEntity(user,
                                                new BackEndInterface.OnOperationCompleteListener() {
                                                    @Override
                                                    public void onSuccess() {
                                                        Toast.makeText(getApplicationContext(), "Hai rimosso " +
                                                                "il feedback", Toast.LENGTH_SHORT).show();
                                                        removeFeedback.setEnabled(false);
                                                        sndFeedBtn.setText("Invia");
                                                        mComment.setText("");
                                                        found = false;
                                                    }

                                                    @Override
                                                    public void onError() {

                                                    }
                                                });
                                    }

                                    @Override
                                    public void onError() {

                                    }
                        });
                    }
                }
            });
        } else{
            feedbackTitle.setVisibility(View.GONE);
            ratingBar.setVisibility(View.GONE);
            textFieldBoxes.setVisibility(View.GONE);
            sndFeedBtn.setVisibility(View.GONE);
            removeFeedback.setVisibility(View.GONE);
        }

        List<Feedback> feedbacks = new ArrayList<>();

        for(int i=0; i<user.getFeedbacks().size(); i++)
        {
            if(!userLogged.getId().equals(user.getFeedbacks().get(i).getIdUser()))
                feedbacks.add(user.getFeedbacks().get(i));
        }

        if(feedbacks.size()<=2){
            goFeedBacksList.setVisibility(View.GONE);
        }

        recyclerView = findViewById(R.id.RecyclerView_Review);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        AdapterReview adapter = new AdapterReview(getApplicationContext(), feedbacks);
        recyclerView.setAdapter(adapter);
    }

    private boolean checkError() {
        boolean alright = true;

        if(user.getFeedbacks().get(position) != null) {
            if (mComment != null &&
                    (mComment.getText().toString().equals(user.getFeedbacks().get(position).getComment())
                            && ratingBar.getRating() == user.getFeedbacks().get(position).getVote())
                    && !user.getFeedbacks().get(position).getComment().isEmpty()
                    && !mComment.getText().toString().isEmpty()) {
                textFieldBoxes.setError("Hai inserito lo stesso commento", true);
                alright = false;
            }
        }

        return alright;
    }
    private void ratingBarListener() {

        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            this.rating = rating;

            if(textFieldBoxes.getVisibility() == View.GONE)
                textFieldBoxes.setVisibility(View.VISIBLE);
        });
    }
}

class AdapterReview extends RecyclerView.Adapter <AdapterReview.MyViewHolder>
{
    private Context context;
    private List<Feedback> feedbacks;
    private LayoutInflater inflater;

    public AdapterReview(Context appContext,List<Feedback> feedbacks)
    {
        this.context = appContext;
        this.feedbacks = feedbacks;
        inflater = (LayoutInflater.from(appContext));
    }

    @NonNull
    @Override
    public AdapterReview.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = inflater.inflate(R.layout.review_layout, parent, false);
        AdapterReview.MyViewHolder vh = new AdapterReview.MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterReview.MyViewHolder holder, int position) {
        holder.displayName.setText(feedbacks.get(position).getDisplayNameUser());
        holder.description.setText(feedbacks.get(position).getComment());
        holder.ratingBar.setRating((float) feedbacks.get(position).getVote());

        User user = new User();
        user.setId(feedbacks.get(position).getIdUser());
        String displayName = feedbacks.get(position).getDisplayNameUser();
        if(displayName.contains(" "))
        {
            user.setName(displayName.substring(0, displayName.indexOf(" ")));
            user.setSurname(displayName.substring(displayName.indexOf(" ")+1));
        }
        else
            user.setName(displayName);
        user.setProfileImageUrl(feedbacks.get(position).getProfileImageUrlUser());

        new ProfileImageFetcher(context).fetchImageOf(user, drawable ->
        {
            if(drawable != null)
                holder.imgProfile.setImageDrawable(drawable);
        });

        //Ascolto l'evento click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            private Blocker mBlocker = new Blocker();

            @Override
            public void onClick(View v) {
                if (!mBlocker.block()) {
                    Intent intent2 = new Intent(v.getContext(), ProfileActivity.class);
                    intent2.putExtra("id_cicerone_to_show",feedbacks.get(position).getIdUser());
                    intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(intent2);
                }
            }
        });
    }

    @Override
    public int getItemCount() {

        if(feedbacks.size()>2)
            return 2;
        else
            return feedbacks.size();

    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView displayName, description;
        ImageView imgProfile;
        RatingBar ratingBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            displayName = itemView.findViewById(R.id.textView_DisplayName);
            description = itemView.findViewById(R.id.textView_Description);
            imgProfile = itemView.findViewById(R.id.imageView_ProfileImage);
            ratingBar = itemView.findViewById(R.id.ratingBar2);
        }
    }
}
