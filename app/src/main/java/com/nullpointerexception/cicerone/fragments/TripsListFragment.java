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
import com.nullpointerexception.cicerone.activities.FindItineraryActivty;
import com.nullpointerexception.cicerone.activities.ItineraryActivity;
import com.nullpointerexception.cicerone.activities.MainActivity;
import com.nullpointerexception.cicerone.components.AuthenticationManager;
import com.nullpointerexception.cicerone.components.BackEndInterface;
import com.nullpointerexception.cicerone.components.Itinerary;
import com.nullpointerexception.cicerone.components.ObjectSharer;
import com.nullpointerexception.cicerone.custom_views.ItineraryView;

import java.util.List;

/**
 *      TripsListFragment
 *
 *      Shows a list with itineraries which user want to participate.
 */
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
    /**   Last adapter size. Used to refresh page only if adapter changes.  */
    private int lastSize = 0;

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
        adapter = new ParticipatedItinerariesAdapter(this,
                AuthenticationManager.get().getUserLogged().getItineraries());
        recyclerView.setAdapter(adapter);

        updateItineraryViews();

        findItineraryFab.setOnClickListener(v -> startActivityForResult(new Intent(getContext(), FindItineraryActivty.class), 0));

        return view;
    }

    private void updateItineraryViews()
    {
        lastSize = adapter.getItemCount();

        adapter.notifyDataSetChanged();
        if(recyclerView.getAdapter() != null)
            noItinerariesLabel.setVisibility(recyclerView.getAdapter().getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(lastSize != adapter.getItemCount())
            updateItineraryViews();
    }
}

class ParticipatedItinerariesAdapter extends RecyclerView.Adapter
{
    private Fragment reference;
    private List<Itinerary> dataSet;

    ParticipatedItinerariesAdapter(Fragment reference, List<Itinerary> dataSet)
    {
        this.reference = reference;
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

        if(dataSet.get(position).getDate() == null ||
                dataSet.get(position).getMeetingTime() == null)
            dataSet.get(position).getFieldsFromId();

        viewHolder.getView().setCiceroneShowed(false);
        viewHolder.setViewFor(dataSet.get(position));

        viewHolder.getView().setOnViewClickListener(() ->
        {
            Itinerary itinerary = viewHolder.getView().getItinerary();

            if(itinerary.getParticipants() == null || itinerary.getParticipants().isEmpty())
            {
                BackEndInterface.get().getEntity(itinerary,
                        new BackEndInterface.OnOperationCompleteListener()
                        {
                            @Override
                            public void onSuccess()
                            {
                                ObjectSharer.get().shareObject("show_trip_as_user", itinerary);
                                reference.startActivityForResult(new Intent(reference.getContext(), ItineraryActivity.class), 0);
                            }

                            @Override
                            public void onError() { }
                        });
            }
            else
            {
                ObjectSharer.get().shareObject("show_trip_as_user", itinerary);
                reference.startActivityForResult(new Intent(reference.getContext(), ItineraryActivity.class), 0);
            }

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
