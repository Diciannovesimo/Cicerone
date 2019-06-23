package com.nullpointerexception.cicerone.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.activities.FeedBacksActivity;
import com.nullpointerexception.cicerone.activities.MainActivity;
import com.nullpointerexception.cicerone.components.AuthenticationManager;
import com.nullpointerexception.cicerone.components.ObjectSharer;
import com.nullpointerexception.cicerone.components.ProfileImageFetcher;
import com.nullpointerexception.cicerone.components.User;

public class ProfileFragment extends Fragment
{
    private TextView mEmail, mTelephone, mDate, mName, mItinerariesAsParticipant,  goFeedBacksList;
    private ImageView profileImage, settings_btn;

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


        goFeedBacksList.setOnClickListener(v -> {
            Intent intent2 = new Intent(getContext(), FeedBacksActivity.class);
            startActivity(intent2);
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
    }

    /**
     * @brief Set text field
     */
    public void setTextField(View v) {

        //Get user form firebase
        User user = AuthenticationManager.get().getUserLogged();

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
        /*settings_btn.setOnClickListener(v1 -> {

        });*/
    }

}
