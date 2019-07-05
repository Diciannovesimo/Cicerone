package com.nullpointerexception.cicerone.fragments;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.activities.ItineraryActivity;
import com.nullpointerexception.cicerone.components.BackEndInterface;
import com.nullpointerexception.cicerone.components.Itinerary;
import com.nullpointerexception.cicerone.components.ObjectSharer;
import com.nullpointerexception.cicerone.custom_views.ItineraryView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchResultsFragment extends Fragment
{

    /*
            UI Components
     */
    private View backButton, noItinerariesFound;
    private TextView title;
    private RecyclerView recyclerView;

    /*
            Variables
     */
    private List<Itinerary> itinerariesList = new Vector();
    private String location, date;
    private RecyclerView.LayoutManager layoutManager;
    private FoundItinerariesAdapter adapter;

    public SearchResultsFragment() { }

    public SearchResultsFragment(String location, String date)
    {
        this.location = location;
        this.date = date;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);

        //  UI components assignments
        noItinerariesFound = view.findViewById(R.id.noItinerariesFound);
        backButton = view.findViewById(R.id.backButton);
        title = view.findViewById(R.id.titleLabel);
        recyclerView = view.findViewById(R.id.itinerariesListContainer);

        //  Initialization

        String dateString;

        try
        {
            Date dt = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).parse(date);
            dateString = new SimpleDateFormat("dd MMM", Locale.getDefault()).format(dt);
        }
        catch (ParseException e)
        {
            Log.e("SearchItinerary", e.toString());
            dateString = date;
        }

        title.setText(String.format("%s %s %s %s", getResources().getString(R.string.searchResultsTitle1),
                location, getResources().getString(R.string.searchResultsTitle2), dateString));

        backButton.setOnClickListener(v ->
        {
            if(getActivity() != null)
                getActivity().finish();
        });

        //      Initialize recyclerView
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FoundItinerariesAdapter(itinerariesList);
        recyclerView.setAdapter(adapter);

        noItinerariesFound.setVisibility(View.GONE);

        //   Add graphic loading animation
        ViewGroup root = (ViewGroup) recyclerView.getRootView();
        FrameLayout frameLayout = new FrameLayout(recyclerView.getContext());
        frameLayout.setBackgroundColor(Color.parseColor("#33000000"));
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        frameLayout.setZ(999);
        LottieAnimationView animationView = new LottieAnimationView(recyclerView.getContext());
        animationView.setAnimation(R.raw.world_loading);
        animationView.loop(true);
        int size = getResources().getDisplayMetrics().widthPixels / 2;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
        params.gravity = Gravity.CENTER;
        animationView.setLayoutParams(params);
        animationView.playAnimation();
        frameLayout.addView(animationView);
        //  Disables interactions with others views under this
        frameLayout.setOnTouchListener((view12, motionEvent) -> true);
        root.addView(frameLayout);

        //  Start search
        BackEndInterface.get().searchItinerariesFor(location, date, itinerariesList,
                new BackEndInterface.OnOperationCompleteListener()
                {
                    @Override
                    public void onSuccess()
                    {
                        //  Remove login animation
                        if(getActivity() != null)
                            getActivity().runOnUiThread(() ->
                            {
                                if(frameLayout.getParent() != null)
                                    ((ViewGroup)frameLayout.getParent()).removeView(frameLayout);
                            });

                        adapter.notifyDataSetChanged();
                        noItinerariesFound.setVisibility(itinerariesList.isEmpty() ? View.VISIBLE : View.GONE);
                    }

                    @Override
                    public void onError()
                    {
                        Log.i("", "");
                    }
                });

        return view;
    }

}

class FoundItinerariesAdapter extends RecyclerView.Adapter
{
    private List<Itinerary> dataSet;

    FoundItinerariesAdapter(List<Itinerary> dataSet)
    {
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new ViewHolder(new ItineraryView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.setViewFor(dataSet.get(position));

        viewHolder.getView().setOnViewClickListener(() ->
        {
            ObjectSharer.get().shareObject("show_trip_as_user", dataSet.get(position));
            viewHolder.getView().getContext().startActivity(
                    new Intent(viewHolder.getView().getContext(), ItineraryActivity.class));
        });
    }

    @Override
    public int getItemCount()
    {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private ItineraryView itineraryView;

        public ViewHolder(ItineraryView v)
        {
            super(v);
            itineraryView = v;
            itineraryView.setEditable(false);
        }

        public void setViewFor(Itinerary itinerary)
        {
            itineraryView.setFrom(itinerary);
        }

        public ItineraryView getView()  { return itineraryView; }
    }

}