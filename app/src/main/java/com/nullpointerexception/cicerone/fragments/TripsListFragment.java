package com.nullpointerexception.cicerone.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.activities.FindItineraryActivty;
import com.nullpointerexception.cicerone.activities.MainActivity;
import com.nullpointerexception.cicerone.components.BackEndInterface;
import com.nullpointerexception.cicerone.components.Itinerary;
import com.nullpointerexception.cicerone.components.User;
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

        //  Add fake data
        Itinerary itinerary = new Itinerary();
        itinerary.setDate("2019/06/13");
        itinerary.setMeetingTime("10:00");
        itinerary.setLocation("Canosa Di Puglia");
        itinerary.setMeetingPlace("Piazza Terme");

        User cicerone = new User();
        cicerone.setId("fQv6mnTt6BTnuULNhv1DeasBOuL2");
        BackEndInterface.get().getEntity(cicerone, new BackEndInterface.OnOperationCompleteListener()
        {
            @Override
            public void onSuccess()
            {
                itinerary.setCicerone(cicerone);
                itineraries.add(itinerary);

                setItineraryViews(itineraries);
            }

            @Override
            public void onError() { }
        });

        findItineraryFab.setOnClickListener(v -> startActivity(new Intent(getContext(), FindItineraryActivty.class)));

        return view;
    }

    private void setItineraryViews(List<Itinerary> itineraries)
    {
        adapter.notifyDataSetChanged();
        noItinerariesLabel.setVisibility(this.itineraries.size() == 0 ? View.VISIBLE : View.GONE);
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

        viewHolder.setViewFor(dataSet.get(position));

        viewHolder.getView().setOnViewClickListener(() ->
        {
            Toast.makeText(viewHolder.getView().getContext(), "Open detail.", Toast.LENGTH_SHORT).show();
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
