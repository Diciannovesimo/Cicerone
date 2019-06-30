package com.nullpointerexception.cicerone.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.components.BackEndInterface;
import com.nullpointerexception.cicerone.components.Blocker;
import com.nullpointerexception.cicerone.components.Itinerary;
import com.nullpointerexception.cicerone.components.ObjectSharer;
import com.nullpointerexception.cicerone.components.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *      ProposedStageActivity
 *
 *      Activity to see proposed stages of itinerary
 *
 *      @author Mattia
 */
public class ProposedStageActivity extends AppCompatActivity
{

    private RecyclerView recyclerView;
    private List<Stage> proposedStage = new ArrayList<Stage>();
    private Itinerary itinerary;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proposed_stage);

        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#FF5500"));

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_ProposedStageActivity);
        setSupportActionBar(toolbar);

        itinerary = (Itinerary) ObjectSharer.get().getSharedObject("lista_proposte");

        proposedStage = itinerary.getProposedStages();

        //Check if actionbar is initialized
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.RecyclerView_ProposedStage);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        if(proposedStage.size() != 0)
        {
            AdapterStage adapter = new AdapterStage(this, proposedStage, itinerary);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObjectSharer.get().remove("lista_proposte");
        ObjectSharer.get().shareObject("show_trip_as_cicerone", itinerary);
    }
}

class AdapterStage extends RecyclerView.Adapter <AdapterStage.MyViewHolder>
{

    private Context context;
    private List<Stage> listPlaces;
    private LayoutInflater inflater;
    private Itinerary itinerary ;

    public AdapterStage(Context appContext, List<Stage> listPlace_test, Itinerary itinerary)
    {
        this.context = appContext;
        this.listPlaces = listPlace_test;
        inflater = (LayoutInflater.from(appContext));
        this.itinerary = itinerary;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = inflater.inflate(R.layout.proposed_stage_activity, parent, false);
        MyViewHolder vh = new MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        holder.name.setText(listPlaces.get(position).getName());
        holder.description.setText(listPlaces.get(position).getDescription());

        //Ascolto l'evento click relativo a "Aggiungi tappa"
        holder.addstage.setOnClickListener(new View.OnClickListener() {
            private Blocker mBlocker = new Blocker();

            @Override
            public void onClick(View v) {
                if (!mBlocker.block()) {
                    //Aggiungo la nuova tappa
                    List<Stage> newStage = itinerary.getStages();
                    newStage.add(listPlaces.get(position));
                    itinerary.setStages(newStage);

                    //Rimuovo l'itinerario da quelli proposti
                    listPlaces.remove(position);
                    notifyItemRemoved(position);
                    itinerary.setProposedStages(listPlaces);

                    //Aggiungere il nuovo itinerario
                    BackEndInterface.get().removeEntity(itinerary, new BackEndInterface.OnOperationCompleteListener()
                    {
                        @Override
                        public void onSuccess()
                        {
                            BackEndInterface.get().storeEntity(itinerary);

                            ObjectSharer.get().shareObject("show_trip_as_cicerone", itinerary);
                            if(context instanceof Activity)
                            {
                                ((Activity) context).setResult(Activity.RESULT_OK);
                            }
                        }

                        @Override
                        public void onError()
                        {

                        }
                    });
                }
            }
        });

        //Ascolto l'evento click relativo a "Visualizza posizione GPS"
        holder.imgGPS.setOnClickListener(new View.OnClickListener() {
            private Blocker mBlocker = new Blocker();

            @Override
            public void onClick(View v) {
                if (!mBlocker.block()) {
                    LatLng coordinates = listPlaces.get(position).getCoordinates();
                    Double lat = coordinates.latitude;
                    Double lng = coordinates.longitude;
                    Intent intent = new Intent(context, showPlaceActivity.class);
                    intent.putExtra("latitude", lat);
                    intent.putExtra("longitude", lng);
                    intent.putExtra("marker", listPlaces.get(position).getName());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listPlaces.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {

        TextView name, description, addstage;
        ImageView imgGPS;


        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);

            name = itemView.findViewById(R.id.textView_Name);
            description = itemView.findViewById(R.id.textView_Description);
            addstage = itemView.findViewById(R.id.textView_AddStage);
            imgGPS = itemView.findViewById(R.id.SeeOnMapButton);

        }
    }
}




