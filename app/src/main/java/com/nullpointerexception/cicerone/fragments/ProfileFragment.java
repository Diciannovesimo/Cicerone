package com.nullpointerexception.cicerone.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.activities.FeedBacksActivity;
import com.nullpointerexception.cicerone.activities.MainActivity;
import com.nullpointerexception.cicerone.activities.ProfileActivity;
import com.nullpointerexception.cicerone.activities.SettingsActivity;
import com.nullpointerexception.cicerone.components.AuthenticationManager;
import com.nullpointerexception.cicerone.components.Blocker;
import com.nullpointerexception.cicerone.components.Feedback;
import com.nullpointerexception.cicerone.components.ObjectSharer;
import com.nullpointerexception.cicerone.components.ProfileImageFetcher;
import com.nullpointerexception.cicerone.components.User;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment
{
    private TextView mEmail, mTelephone, mDate, mName, mItinerariesAsParticipant,  goFeedBacksList, feedbakcTitle;
    private ImageView profileImage, settings_btn;
    private RecyclerView recyclerView;
    private RatingBar ratingBarAVG;

    public ProfileFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        if(getActivity() != null && ((MainActivity) getActivity()).getSupportActionBar() != null)
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.menu_profile);

        //Initialize UI
        initUI(view);

        //Set text in the field
        setTextField(view);

        goFeedBacksList.setOnClickListener(new View.OnClickListener() {
            private Blocker mBlocker = new Blocker();

            @Override
            public void onClick(View v) {
                if (!mBlocker.block()) {
                    Intent intent2 = new Intent(getContext(), FeedBacksActivity.class);
                    startActivity(intent2);
                }
            }
        });

        return view;
    }

    public void initUI(View v) {
        mEmail = v.findViewById(R.id.profileMail_tv);
        mTelephone = v.findViewById(R.id.profilePhone_tv);
        mDate = v.findViewById(R.id.profileDate_tv);
        mName = v.findViewById(R.id.profileName_tv);
        mItinerariesAsParticipant = v.findViewById(R.id.itinerariesAsParticipant_tv);
        profileImage = v.findViewById(R.id.us_image);
        settings_btn = v.findViewById(R.id.settings_btn);
        goFeedBacksList = v.findViewById(R.id.goFeedBacksList);
        ratingBarAVG = v.findViewById(R.id.ratingBarAVG);
        feedbakcTitle = v.findViewById(R.id.feedbackListTitle_tv);
    }

    @Override
    public void onResume() {
        super.onResume();

        View view = null;
        if(ObjectSharer.get().getSharedObject("view") != null)
            view = (View) ObjectSharer.get().getSharedObject("view");

        if(view != null)
            setTextField(view);

        ObjectSharer.get().remove("view");
    }

    /**
     * @brief Set text field
     */
    public void setTextField(View v) {

        //Get user form firebase
        User user = AuthenticationManager.get().getUserLogged();

        ratingBarAVG.setRating(user.getAverageFeedback());

        new ProfileImageFetcher(v.getContext())
                .fetchImageOf(user, drawable -> {
                    profileImage.setImageDrawable(drawable);
                });

        //Set name field
        mName.setText(user.getDisplayName());

        //set email
        if(!user.getEmail().isEmpty()) {
            mEmail.setText(user.getEmail());
        }

        //set dateBirth
        mDate.setText(user.getDateBirth());

        //Set the number of itinerary in which the user participated
        mItinerariesAsParticipant.setText(String.valueOf(user.getItineraries().size()));

        //Set telephone number
        mTelephone.setText(user.getPhoneNumber());

        if(user.getFeedbacks().size()<=2){
            goFeedBacksList.setVisibility(View.GONE);
        }

        //Set click listener for settings button
        settings_btn.setOnClickListener(new View.OnClickListener() {
            private Blocker mBlocker = new Blocker();

            @Override
            public void onClick(View v) {
                if (!mBlocker.block()) {
                    startActivity(new Intent(getContext(), SettingsActivity.class));
                    ObjectSharer.get().shareObject("view", v);
                }
            }
        });
        
        List<Feedback> feedbacks = new ArrayList<>();

        for(int i=0; i<user.getFeedbacks().size(); i++)
        {
            if(!user.getId().equals(user.getFeedbacks().get(i).getIdUser()))
                feedbacks.add(user.getFeedbacks().get(i));
        }

        if(feedbacks.size()<=2)
            goFeedBacksList.setVisibility(View.GONE);

        if(feedbacks.size() == 0)
            feedbakcTitle.setVisibility(View.GONE);

        recyclerView = v.findViewById(R.id.RecyclerView_Review);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(v.getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        AdapterReviewProfileFragment adapter = new AdapterReviewProfileFragment(v.getContext(), feedbacks);
        recyclerView.setAdapter(adapter);
    }

}

class AdapterReviewProfileFragment extends RecyclerView.Adapter <AdapterReviewProfileFragment.MyViewHolder>
{
    private Context context;
    private List<Feedback> feedbacks;
    private LayoutInflater inflater;

    public AdapterReviewProfileFragment(Context appContext,List<Feedback> feedbacks)
    {
        this.context = appContext;
        this.feedbacks = feedbacks;
        inflater = (LayoutInflater.from(appContext));
    }

    @NonNull
    @Override
    public AdapterReviewProfileFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = inflater.inflate(R.layout.review_layout, parent, false);
        AdapterReviewProfileFragment.MyViewHolder vh = new AdapterReviewProfileFragment.MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterReviewProfileFragment.MyViewHolder holder, int position) {
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

    public class MyViewHolder extends RecyclerView.ViewHolder{

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
