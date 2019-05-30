package com.nullpointerexception.cicerone.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.activities.ItineraryActivity;
import com.nullpointerexception.cicerone.activities.ItineraryEditActivity;
import com.nullpointerexception.cicerone.activities.MainActivity;
import com.nullpointerexception.cicerone.components.AuthenticationManager;
import com.nullpointerexception.cicerone.components.Itinerary;
import com.nullpointerexception.cicerone.components.Stage;

import java.util.ArrayList;
import java.util.List;

public class ItinerariesListFragment extends Fragment
{
    //Test things
    private Button editItinerary_test;
    private Itinerary testItinerary;
    private Stage stage1, stage2, stage3;
    private List<Stage> listPlace_test = new ArrayList<>();

    /*
            Views
     */
    private RecyclerView itinerariesList;
    private FloatingActionButton newItineraryButton;

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

        itinerariesList = view.findViewById(R.id.itinerariesListContainer);
        newItineraryButton = view.findViewById(R.id.newItineraryButton);

        //Inizializzo test
        editItinerary_test = view.findViewById(R.id.editItinerary_testButton);

        testItinerary = new Itinerary();
        stage1 = new Stage();
        stage2 = new Stage();
        stage3 = new Stage();

        testItinerary.setLocation("Bari");
        testItinerary.setCurrency("€");
        testItinerary.setDate("30/5/2019");
        testItinerary.setDescription("prova");
        testItinerary.setLanguage("Italiano");
        testItinerary.setMaxParticipants(10);
        testItinerary.setMeetingPlace("Corso Cavour, Bari BA, Italy");
        testItinerary.setMeetingTime("12:7");
        testItinerary.setPrice(0);
        testItinerary.setIdCicerone(AuthenticationManager.get().getUserLogged().getId());
        testItinerary.generateId();

        stage1.setAddress("Corso Italia, 15, 70122 Bari BA, Italy");
        stage1.setDescription("prova2");
        stage1.setName("Multicinema Galleria");
        stage1.setCoordinates(new LatLng(41.1187193, 16.8664923));

        stage2.setAddress("Corso Cavour, 12, 70122 Bari BA, Italy");
        stage2.setDescription("prova1");
        stage2.setName("Teatro Petruzzelli");
        stage2.setCoordinates(new LatLng(41.123568, 16.8709473));

        stage3.setAddress("Piazza Mercantile, 70, 70122 Bari BA, Italy");
        stage3.setDescription("prova");
        stage3.setName("Column of Shame or Column of Justice");
        stage3.setCoordinates(new LatLng(41.128064, 16.8699213));

        listPlace_test.add(stage1);
        listPlace_test.add(stage2);
        listPlace_test.add(stage3);

        testItinerary.setStages(listPlace_test);

        newItineraryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), ItineraryActivity.class));
            }
        });

        editItinerary_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ItineraryEditActivity.class);
                Gson gson = new Gson();
                String itineratyJson = gson.toJson(testItinerary);
                intent.putExtra("test_itinerary", itineratyJson);
                startActivity(intent);
            }
        });

        return view;
    }
}
