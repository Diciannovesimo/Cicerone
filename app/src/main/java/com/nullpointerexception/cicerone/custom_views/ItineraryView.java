package com.nullpointerexception.cicerone.custom_views;

import android.content.Context;
import android.view.MotionEvent;
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

        setOnTouchListener((view, motionEvent) ->
        {
            switch (motionEvent.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    setScaleX(0.98f);
                    setScaleY(0.98f);
                    setAlpha(0.85f);
                    break;

                case MotionEvent.ACTION_CANCEL:
                    setScaleX(1f);
                    setScaleY(1f);
                    setAlpha(1f);
                    break;

                case MotionEvent.ACTION_UP:
                    setScaleX(1f);
                    setScaleY(1f);
                    setAlpha(1f);
                    performClick();
                    break;
            }

            return false;
        });

    }

    public void setFrom(Itinerary itinerary)
    {
        city.setText(itinerary.getLocation());
        date.setText(itinerary.getDate());
        meeting.setText(itinerary.getMeetingPlace() + " - " + itinerary.getMeetingTime());
        cicerone.setVisibility(GONE);
    }
}
