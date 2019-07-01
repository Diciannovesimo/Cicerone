package com.nullpointerexception.cicerone.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.fragments.FindItineraryActivityFragment;
import com.nullpointerexception.cicerone.fragments.SearchResultsFragment;

/**
 * TODO: Documenta
 */
public class FindItineraryActivty extends AppCompatActivity
        implements FindItineraryActivityFragment.OnUIInteractionListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_itinerary_activty_activity);
        if (savedInstanceState == null)
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new FindItineraryActivityFragment())
                    .commitNow();
        }
    }

    @Override
    public void onButtonSearchPressed(String place, String date)
    {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new SearchResultsFragment(place, date))
                .commitNow();
    }
}
