package com.nullpointerexception.cicerone.custom_views;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.nullpointerexception.cicerone.R;

public class ItineraryView extends FrameLayout
{

    public ItineraryView(@NonNull Context context)
    {
        super(context);
        addView( inflate(context, R.layout.itinerary_layout, null) );



    }
}
