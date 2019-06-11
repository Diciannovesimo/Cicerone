package com.nullpointerexception.cicerone.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;
import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.activities.MainActivity;
import com.nullpointerexception.cicerone.activities.ParticipantsActivity;
import com.nullpointerexception.cicerone.activities.ProposedStageActivity;
import com.nullpointerexception.cicerone.components.BackEndInterface;
import com.nullpointerexception.cicerone.components.Itinerary;
import com.nullpointerexception.cicerone.components.ObjectSharer;
import com.nullpointerexception.cicerone.components.Stage;
import com.nullpointerexception.cicerone.components.User;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment
{
    private Button button1, button2;

    public ProfileFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        if(getActivity() != null && ((MainActivity) getActivity()).getSupportActionBar() != null)
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.menu_profile);

        button1 = view.findViewById(R.id.button1);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(v.getContext(), ParticipantsActivity.class));

            }
        });

        button2 = view.findViewById(R.id.button2);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                User cicerone = new User();
                cicerone.setId("fQv6mnTt6BTnuULNhv1DeasBOuL2");


                BackEndInterface.get().getEntity(cicerone, new BackEndInterface.OnOperationCompleteListener()
                {
                    @Override
                    public void onSuccess()
                    {
                        itinerary.setCicerone(cicerone);
                        itinerary.generateId();

                    }

                    @Override
                    public void onError() { }
                });

                Stage stage1 = new Stage();
                Stage stage2 = new Stage();
                Stage stageproposta = new Stage();

                stageproposta.setAddress("Casa di vito");
                stageproposta.setDescription("vito");
                stageproposta.setName("Vito cus");
                stageproposta.setCoordinates(new LatLng(41.118453, 16.865433));

                stage1.setAddress("Corso Italia, 15, 70122 Bari BA, Italy");
                stage1.setDescription("prova");
                stage1.setName("Teatro Petruzzelli");
                stage1.setCoordinates(new LatLng(41.1187193, 16.8664923));

                stage2.setAddress("Piazza Aldo Moro, Bari BA, Italy");
                stage2.setDescription("lel\n");
                stage2.setName("Piazza Aldo Moro");
                stage2.setCoordinates(new LatLng(41.123568, 16.8709473));

                List<Stage> listPlace_test = new ArrayList<Stage>();

                listPlace_test.add(stage1);
                listPlace_test.add(stage2);

                itinerary.setStages(listPlace_test);

                List<Stage> proposte = new ArrayList<Stage>();
                proposte.add(stageproposta);
                itinerary.setProposedStages(proposte);

                ObjectSharer.get().shareObject("vito", itinerary);

                startActivity(new Intent(v.getContext(), ProposedStageActivity.class));

            }
        });

        return view;
    }

}
