package com.nullpointerexception.cicerone.fragments;

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
import com.nullpointerexception.cicerone.activities.ItineraryActivity;
import com.nullpointerexception.cicerone.activities.MainActivity;
import com.nullpointerexception.cicerone.components.Itinerary;
import com.nullpointerexception.cicerone.custom_views.ItineraryView;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class ItinerariesListFragment extends Fragment
{
    /*
            Views
     */
    private View noItinerariesLabel;
    private RecyclerView recyclerView;
    private FloatingActionButton newItineraryButton;

    /*
            Vars
     */
    private RecyclerView.LayoutManager layoutManager;
    private Adapter adapter;
    private List<Itinerary> itineraries = new Vector<>();

    public ItinerariesListFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_itineraries_list, container, false);

        if(getActivity() != null && ((MainActivity) getActivity()).getSupportActionBar() != null)
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.menu_itineraries_list);

        recyclerView = view.findViewById(R.id.itinerariesListContainer);
        newItineraryButton = view.findViewById(R.id.newItineraryButton);
        noItinerariesLabel = view.findViewById(R.id.noItineraries);

        /*
                Initialize recyclerView
         */
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new Adapter(itineraries);
        recyclerView.setAdapter(adapter);

        newItineraryButton.setOnClickListener(view1 ->
                startActivity(new Intent(getActivity(), ItineraryActivity.class)));

        return view;
    }

    private void addItinerary(Itinerary itinerary)
    {
        itineraries.add(itinerary);
        adapter.notifyItemInserted( itineraries.size()-1 );
        noItinerariesLabel.setVisibility(View.GONE);
    }

    private void addItineraries(Itinerary... itineraries)
    {
        int startIndex = this.itineraries.size();
        this.itineraries.addAll(Arrays.asList(itineraries));
        adapter.notifyItemRangeInserted(startIndex, this.itineraries.size());
        noItinerariesLabel.setVisibility(View.GONE);
    }

    private void removeItinerary(int i)
    {
        itineraries.remove(i);
        adapter.notifyItemRemoved(i);
        noItinerariesLabel.setVisibility(itineraries.size() == 0 ? View.VISIBLE : View.GONE);
    }
}

class Adapter extends RecyclerView.Adapter
{
    private List<Itinerary> dataSet;

    Adapter(List<Itinerary> dataSet)
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
        ((ViewHolder) holder).setViewFor(dataSet.get(position));
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
        }

        public void setViewFor(Itinerary itinerary)
        {
            itineraryView.setFrom(itinerary);
        }
    }

}
