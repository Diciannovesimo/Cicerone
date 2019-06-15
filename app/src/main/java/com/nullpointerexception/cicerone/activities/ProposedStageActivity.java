package com.nullpointerexception.cicerone.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.components.BackEndInterface;
import com.nullpointerexception.cicerone.components.Itinerary;
import com.nullpointerexception.cicerone.components.ObjectSharer;
import com.nullpointerexception.cicerone.components.ProfileImageFetcher;
import com.nullpointerexception.cicerone.components.Stage;
import com.nullpointerexception.cicerone.components.User;

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
public class ProposedStageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Stage> proposedStage = new ArrayList<Stage>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proposed_stage);

        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#FF5500"));

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_ProposedStageActivity);
        setSupportActionBar(toolbar);

        Itinerary itinerary = (Itinerary) ObjectSharer.get().getSharedObject("lista_proposte");

        proposedStage = itinerary.getProposedStages();

        //Check if actionbar is initialized
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.RecyclerView_ProposedStage);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        if(proposedStage.size() != 0)
        {
            AdapterStage adapter = new AdapterStage(getApplicationContext(), proposedStage, itinerary);
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
    }
}

class AdapterStage extends RecyclerView.Adapter <AdapterStage.MyViewHolder>{

    private Context context;
    private List<Stage> listPlace_test;
    private LayoutInflater inflater;
    private Itinerary itinerary ;

    public AdapterStage(Context appContext, List<Stage> listPlace_test, Itinerary itinerary)
    {
        this.context = appContext;
        this.listPlace_test = listPlace_test;
        inflater = (LayoutInflater.from(appContext));
        this.itinerary = itinerary;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.proposed_stage_activity, parent, false);
        MyViewHolder vh = new MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.name.setText(listPlace_test.get(position).getName());
        holder.description.setText(listPlace_test.get(position).getDescription());

        //Ascolto l'evento click relativo a "Aggiungi tappa"
        holder.addstage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Aggiungo la nuova tappa
                List<Stage> newStage = itinerary.getStages();
                newStage.add(listPlace_test.get(position));
                itinerary.setStages(newStage);

                //Rimuovo l'itinerario da quelli proposti
                listPlace_test.remove(position);

                itinerary.setProposedStages(listPlace_test);

                //Aggiungere il nuovo itinerario
                BackEndInterface.get().removeEntity(itinerary, new BackEndInterface.OnOperationCompleteListener() {
                    @Override
                    public void onSuccess() {
                        BackEndInterface.get().storeEntity(itinerary);
                        v.getContext().startActivity(new Intent(v.getContext(), ProposedStageActivity.class));

                    }

                    @Override
                    public void onError() {

                    }
                });
            }
        });

        //Ascolto l'evento click relativo a "Visualizza posizione GPS"
        holder.imgGPS.setOnClickListener(v -> {
            LatLng coordinates = listPlace_test.get(position).getCoordinates();
            Double lat = coordinates.latitude;
            Double lng = coordinates.longitude;
            Intent intent = new Intent(v.getContext(), showPlaceActivity.class);
            intent.putExtra("latitude", lat);
            intent.putExtra("longitude", lng);
            intent.putExtra("marker", listPlace_test.get(position).getName());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listPlace_test.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name, description, addstage;
        ImageView imgGPS;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.textView_Name);
            description = itemView.findViewById(R.id.textView_Description);
            addstage = itemView.findViewById(R.id.textView_AddStage);
            imgGPS = itemView.findViewById(R.id.SeeOnMapButton);

        }
    }
}




