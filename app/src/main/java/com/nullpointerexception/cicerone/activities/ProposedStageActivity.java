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
import com.nullpointerexception.cicerone.components.Itinerary;
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
    private List<Stage> listPlace_test = new ArrayList<Stage>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proposed_stage);

        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#FF5500"));

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_ProposedStageActivity);
        setSupportActionBar(toolbar);

        //Creazione itinerario
        Itinerary itinerary = new Itinerary();

        itinerary.setDate("2019/06/13");
        itinerary.setMeetingTime("10:00");
        itinerary.setLocation("Bari");
        itinerary.setMeetingPlace("Corso Cavour");
        itinerary.setCurrency("€");
        itinerary.setDescription("È il nono comune italiano per popolazione, terzo del Mezzogiorno dopo Napoli e Palermo. La sua area metropolitana è la sesta d'Italia con quasi 1 300 000 abitanti[4].\n" +
                "\n" +
                "È nota anche per essere la città nella quale riposano le reliquie di San Nicola. Tale condizione ha reso Bari e la sua basilica uno dei centri prediletti dalla Chiesa ortodossa in Occidente e anche un importante centro di comunicazione interconfessionale tra l'Ortodossia e il Cattolicesimo.");
        itinerary.setLanguage("Italiano");

        Stage stage1 = new Stage();
        Stage stage2 = new Stage();
        Stage stage3 = new Stage();

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

        itinerary.setStages(listPlace_test);




        //Check if actionbar is initialized
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.RecyclerView_ProposedStage);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        AdapterStage adapter = new AdapterStage(getApplicationContext(), listPlace_test);
        recyclerView.setAdapter(adapter);





    }

    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return super.onSupportNavigateUp();
    }

}

class AdapterStage extends RecyclerView.Adapter <AdapterStage.MyViewHolder>{

    private Context context;
    private List<Stage> listPlace_test;
    private LayoutInflater inflater;

    public AdapterStage(Context appContext, List<Stage> listPlace_test)
    {
        this.context = appContext;
        this.listPlace_test = listPlace_test;
        inflater = (LayoutInflater.from(appContext));
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
                Toast.makeText(context,listPlace_test.get(position).getName() + listPlace_test.get(position).getDescription(), Toast.LENGTH_LONG).show();
            }
        });

        //Ascolto l'evento click relativo a "Visualizza posizione GPS"
        holder.imgGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,listPlace_test.get(position).getName() + listPlace_test.get(position).getDescription(), Toast.LENGTH_LONG).show();
            }
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

            name = (TextView) itemView.findViewById(R.id.textView_Name);
            description = (TextView) itemView.findViewById(R.id.textView_Description);
            addstage = (TextView) itemView.findViewById(R.id.textView_AddStage);
            imgGPS = (ImageView) itemView.findViewById(R.id.SeeOnMapButton);

        }
    }
}




