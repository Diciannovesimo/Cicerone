package com.nullpointerexception.cicerone.custom_views;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.components.Itinerary;

public class ItineraryView extends FrameLayout
{

    private TextView city, date, meeting, cicerone;

    public ItineraryView(@NonNull Context context)
    {
        super(context);
        addView( inflate(context, R.layout.itinerary_layout, null) );

        setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        city = findViewById(R.id.cityLabel);
        date = findViewById(R.id.dateLabel);
        meeting = findViewById(R.id.meetingLabel);
        cicerone = findViewById(R.id.ciceroneLabel);

    }

    public void setFrom(Itinerary itinerary)
    {
        city.setText(itinerary.getLocation());
        date.setText(itinerary.getDate());
        meeting.setText(itinerary.getMeetingPlace() + " - " + itinerary.getMeetingTime());
        cicerone.setVisibility(GONE);
    }
}
