package com.nullpointerexception.cicerone.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.activities.ItineraryActivity;
import com.nullpointerexception.cicerone.activities.MainActivity;
import com.nullpointerexception.cicerone.components.AuthenticationManager;
import com.nullpointerexception.cicerone.components.BackEndInterface;
import com.nullpointerexception.cicerone.components.Itinerary;
import com.nullpointerexception.cicerone.components.ObjectSharer;
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_itineraries_list, container, false);

        if(getActivity() != null && ((MainActivity) getActivity()).getSupportActionBar() != null)
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.menu_itineraries_list);

        /*
                Assignments
         */
        recyclerView = view.findViewById(R.id.itinerariesListContainer);
        newItineraryButton = view.findViewById(R.id.newItineraryButton);
        noItinerariesLabel = view.findViewById(R.id.noItineraries);

        /*
                Get itineraries from database
         */

        //   Add graphic loading animation
        ViewGroup root = (ViewGroup) recyclerView.getRootView();
        FrameLayout frameLayout = new FrameLayout(recyclerView.getContext());
        frameLayout.setBackgroundColor(Color.parseColor("#33000000"));
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        frameLayout.setZ(999);
        LottieAnimationView animationView = new LottieAnimationView(recyclerView.getContext());
        animationView.setAnimation(R.raw.loading);
        animationView.loop(true);
        int size = getResources().getDisplayMetrics().widthPixels / 4;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
        params.gravity = Gravity.CENTER;
        animationView.setLayoutParams(params);
        animationView.playAnimation();
        frameLayout.addView(animationView);
        //  Disables interactions with others views under this
        frameLayout.setOnTouchListener((view12, motionEvent) -> true);
        root.addView(frameLayout);

        //  Get itineraries
        if(AuthenticationManager.get().getUserLogged() != null)
        BackEndInterface.get().getItinerariesOf(AuthenticationManager.get().getUserLogged().getId(),
                itineraries, new BackEndInterface.OnOperationCompleteListener() 
                {
                    @Override
                    public void onSuccess() 
                    {
                        //  Remove login animation
                        if(getActivity() != null)
                            getActivity().runOnUiThread(() ->
                            {
                                if(frameLayout.getParent() != null)
                                    ((ViewGroup)frameLayout.getParent()).removeView(frameLayout);
                            });

                        setItineraryViews(itineraries);
                    }

                    @Override
                    public void onError() 
                    {
                        if(getActivity() != null)
                            getActivity().runOnUiThread(() -> 
                            {
                                Toast.makeText(getContext(), R.string.generic_error, Toast.LENGTH_SHORT).show();
                            });
                    }
                });

        /*
                Initialize recyclerView
         */
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new Adapter(itineraries);
        recyclerView.setAdapter(adapter);

        newItineraryButton.setOnClickListener(view1 ->
                startActivityForResult(new Intent(getActivity(), ItineraryActivity.class), 0));

        return view;
    }

    private void addItinerary(Itinerary itinerary)
    {
        itineraries.add(itinerary);
        adapter.notifyItemInserted( itineraries.size()-1 );
        noItinerariesLabel.setVisibility(itineraries.size() == 0 ? View.VISIBLE : View.GONE);
    }

    private void addItineraries(Itinerary... itineraries)
    {
        int startIndex = this.itineraries.size();
        this.itineraries.addAll(Arrays.asList(itineraries));
        adapter.notifyItemRangeInserted(startIndex, this.itineraries.size());
        noItinerariesLabel.setVisibility(this.itineraries.size() == 0 ? View.VISIBLE : View.GONE);
    }

    private void setItineraryViews(List<Itinerary> itineraries)
    {
        adapter.notifyDataSetChanged();
        noItinerariesLabel.setVisibility(this.itineraries.size() == 0 ? View.VISIBLE : View.GONE);
    }

    private void removeItinerary(int i)
    {
        itineraries.remove(i);
        adapter.notifyItemRemoved(i);
        noItinerariesLabel.setVisibility(itineraries.size() == 0 ? View.VISIBLE : View.GONE);
    }

    /*  TODO : Cancellando questa parte far eliminare da Claudio lo share object nell'activity di creazione itinerario.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {


        Object obj = ObjectSharer.get().getSharedObject("new_itinerary");
        if(obj != null)
        {
            addItinerary( (Itinerary) obj );
            ObjectSharer.get().remove("new_itinerary");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }*/
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
        ((ViewHolder) holder).getView().setOnClickListener(view ->
        {
            Context context = ((ViewHolder) holder).getView().getContext();
            ObjectSharer.get().shareObject("edit_itinerary", dataSet.get(position));
            context.startActivity(new Intent(context, ItineraryActivity.class));
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
        }

        public void setViewFor(Itinerary itinerary)
        {
            itineraryView.setFrom(itinerary);
        }

        public ItineraryView getView()  { return itineraryView; }
    }

}
