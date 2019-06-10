package com.nullpointerexception.cicerone.activities;

import android.content.Context;
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
 *      ParticipantsActivity
 *
 *      Activity to see participants of itinerary
 *
 *      @author Mattia
 */
public class ParticipantsActivity extends AppCompatActivity {

    private List<User> participants = new ArrayList<User>();


    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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



        //Creazione utenti
        User[] users = {new User(), new User(), new User(), new User(), new User()};
        users[0].setName("Nome1");
        users[0].setSurname("Cognome1");
        users[0].setProfileImageUrl("https://lh5.googleusercontent.com/-g-k6Y2gG8L4/AAAAAAAAAAI/AAAAAAAAAAA/ACHi3rcvTH3L0elfOy3ojFz4CYiq8YQ0xA/s96-c/photo.jpg");
        participants.add(users[0]);
        users[1].setName("Nome2");
        users[1].setSurname("Cognome2");
        users[1].setProfileImageUrl("https://lh5.googleusercontent.com/-g-k6Y2gG8L4/AAAAAAAAAAI/AAAAAAAAAAA/ACHi3rcvTH3L0elfOy3ojFz4CYiq8YQ0xA/s96-c/photo.jpg");
        participants.add(users[1]);
        users[2].setName("vito");
        users[2].setSurname("Cognome3");
        // users[2].setProfileImageUrl("https://lh5.googleusercontent.com/-g-k6Y2gG8L4/AAAAAAAAAAI/AAAAAAAAAAA/ACHi3rcvTH3L0elfOy3ojFz4CYiq8YQ0xA/s96-c/photo.jpg");
        participants.add(users[2]);
        users[3].setName("Nome4");
        users[3].setSurname("Cognome4");
        users[3].setProfileImageUrl("https://lh5.googleusercontent.com/-g-k6Y2gG8L4/AAAAAAAAAAI/AAAAAAAAAAA/ACHi3rcvTH3L0elfOy3ojFz4CYiq8YQ0xA/s96-c/photo.jpg");
        participants.add(users[3]);
        users[4].setName("Nome5");
        users[4].setSurname("Cognome5");
        users[4].setProfileImageUrl("https://lh5.googleusercontent.com/-g-k6Y2gG8L4/AAAAAAAAAAI/AAAAAAAAAAA/ACHi3rcvTH3L0elfOy3ojFz4CYiq8YQ0xA/s96-c/photo.jpg");
        participants.add(users[4]);


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

        List<Stage> listPlace_test = new ArrayList<>();

        listPlace_test.add(stage1);
        listPlace_test.add(stage2);
        listPlace_test.add(stage3);

        itinerary.setStages(listPlace_test);

        itinerary.setParticipants(participants);

        recyclerView = findViewById(R.id.RecyclerView_Participants);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        Adapter adapter = new Adapter(getApplicationContext(), itinerary.getParticipants());
        recyclerView.setAdapter(adapter);


    }

    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return super.onSupportNavigateUp();
    }
}



class Adapter extends RecyclerView.Adapter <Adapter.MyViewHolder>{

    private Context context;
    private List<User> participants;
    private LayoutInflater inflater;

    public Adapter(Context appContext,List<User> participants)
    {
        this.context = appContext;
        this.participants = participants;
        inflater = (LayoutInflater.from(appContext));
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.participant_layout, parent, false);
        MyViewHolder vh = new MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.name.setText(participants.get(position).getName());
        holder.surname.setText(participants.get(position).getSurname());
        //holder.contorno.setImageResource(R.drawable.contornovero);


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
        ImageView imgProfile, contorno;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.textView_Name);
            surname = (TextView) itemView.findViewById(R.id.textView_Surname);
            imgProfile = (ImageView) itemView.findViewById(R.id.imageView_ProfileImage);
            //contorno = (ImageView) itemView.findViewById(R.id.imageView_Contorno);

        }
    }
}
