package com.nullpointerexception.cicerone.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.components.BackEndInterface;
import com.nullpointerexception.cicerone.components.Itinerary;
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

        //  Start search
        BackEndInterface.get().searchItinerariesFor(location, date, itinerariesList,
                new BackEndInterface.OnOperationCompleteListener()
                {
                    @Override
                    public void onSuccess()
                    {
                        //  TODO: Add loading animation

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