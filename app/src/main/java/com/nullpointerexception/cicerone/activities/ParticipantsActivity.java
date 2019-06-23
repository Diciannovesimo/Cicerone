package com.nullpointerexception.cicerone.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.components.AuthenticationManager;
import com.nullpointerexception.cicerone.components.Itinerary;
import com.nullpointerexception.cicerone.components.ObjectSharer;
import com.nullpointerexception.cicerone.components.ProfileImageFetcher;
import com.nullpointerexception.cicerone.components.User;

import java.util.ArrayList;
import java.util.List;

/**
 *      ParticipantsActivity
 *
 *      Activity to see participants of itinerary
 *
 *      @author Mattia
 */
public class ParticipantsActivity extends AppCompatActivity
{

    private List<User> participants = new ArrayList<User>();


    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants);

        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#FF5500"));

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_ParticipantsActivity);
        setSupportActionBar(toolbar);

        //Check if actionbar is initialized
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Itinerary itinerary = (Itinerary)ObjectSharer.get().getSharedObject("lista_proposte");

        recyclerView = findViewById(R.id.RecyclerView_Participants);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        AdapterPartecipants adapter = new AdapterPartecipants(getApplicationContext(), itinerary.getParticipants());
        recyclerView.setAdapter(adapter);


    }

    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        ObjectSharer.get().remove("lista_proposte");
    }
}

class AdapterPartecipants extends RecyclerView.Adapter <AdapterPartecipants.MyViewHolder>
{

    private Context context;
    private List<User> participants;
    private LayoutInflater inflater;

    public AdapterPartecipants(Context appContext,List<User> participants)
    {
        this.context = appContext;
        this.participants = participants;
        inflater = (LayoutInflater.from(appContext));
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = inflater.inflate(R.layout.participant_layout, parent, false);
        MyViewHolder vh = new MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.name.setText(participants.get(position).getName());
        holder.surname.setText(participants.get(position).getSurname());


        new ProfileImageFetcher(context).fetchImageOf(participants.get(position), drawable ->
        {

            if(drawable != null)
                holder.imgProfile.setImageDrawable(drawable);
        });


        //Ascolto l'evento click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,participants.get(position).getName() + participants.get(position).getSurname(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name, surname;
        ImageView imgProfile;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.textView_Name);
            surname = (TextView) itemView.findViewById(R.id.textView_Surname);
            imgProfile = (ImageView) itemView.findViewById(R.id.imageView_ProfileImage);

        }
    }


}
