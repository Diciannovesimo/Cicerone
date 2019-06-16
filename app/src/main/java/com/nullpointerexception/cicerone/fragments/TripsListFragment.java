package com.nullpointerexception.cicerone.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.activities.FindItineraryActivty;
import com.nullpointerexception.cicerone.activities.ItineraryActivity;
import com.nullpointerexception.cicerone.activities.MainActivity;
import com.nullpointerexception.cicerone.components.AuthenticationManager;
import com.nullpointerexception.cicerone.components.Itinerary;
import com.nullpointerexception.cicerone.components.ObjectSharer;
import com.nullpointerexception.cicerone.custom_views.ItineraryView;

import java.util.List;
import java.util.Vector;

public class TripsListFragment extends Fragment
{

    /*
            UI Components
     */
    private RecyclerView recyclerView;
    private FloatingActionButton findItineraryFab;
    private View noItinerariesLabel;

    /*
            Vars
     */
    private RecyclerView.LayoutManager layoutManager;
    private ParticipatedItinerariesAdapter adapter;
    private List<Itinerary> itineraries = new Vector<>();

    public TripsListFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_trips_list, container, false);

        if(getActivity() != null && ((MainActivity) getActivity()).getSupportActionBar() != null)
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.menu_trips_list);

        recyclerView = view.findViewById(R.id.participatedItinerariesList);
        findItineraryFab = view.findViewById(R.id.findItineraryButton);
        noItinerariesLabel = view.findViewById(R.id.noItineraries);

        /*
                Initialize recyclerView
         */
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ParticipatedItinerariesAdapter(itineraries);
        recyclerView.setAdapter(adapter);

        itineraries.addAll(AuthenticationManager.get().getUserLogged().getItineraries());
        updateItineraryViews();

        //  TODO: Aggiornare in onActivityResult() in caso ci si disiscrive dalla schermata di ricerca?

        findItineraryFab.setOnClickListener(v -> startActivity(new Intent(getContext(), FindItineraryActivty.class)));

        return view;
    }

    private void updateItineraryViews()
    {
        adapter.notifyDataSetChanged();
        if(recyclerView.getAdapter() != null)
            noItinerariesLabel.setVisibility(recyclerView.getAdapter().getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

}

class ParticipatedItinerariesAdapter extends RecyclerView.Adapter
{
    private List<Itinerary> dataSet;

    ParticipatedItinerariesAdapter(List<Itinerary> dataSet)
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

        if(dataSet.get(position).getMeetingPlace() == null)
            dataSet.get(position).getFieldsFromId();

        viewHolder.setViewFor(dataSet.get(position));

        viewHolder.getView().setOnViewClickListener(() ->
        {
            ObjectSharer.get().shareObject("show_trip_as_user", viewHolder.getView().getItinerary());
            Context context = viewHolder.getView().getContext();
            context.startActivity(new Intent(context, ItineraryActivity.class));
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
