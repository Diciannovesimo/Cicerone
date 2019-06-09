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
import com.kinda.alert.KAlertDialog;
import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.activities.ItineraryCreationActivity;
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
    private ItinerariesAdapter adapter;
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
                                    Toast.makeText(getContext(), R.string.generic_error, Toast.LENGTH_SHORT).show());
                    }
                });

        /*
                Initialize recyclerView
         */
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ItinerariesAdapter(itineraries);
        recyclerView.setAdapter(adapter);

        newItineraryButton.setOnClickListener(view1 ->
                startActivityForResult(new Intent(getActivity(), ItineraryCreationActivity.class), 0));

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

}

class ItinerariesAdapter extends RecyclerView.Adapter
{
    private List<Itinerary> dataSet;

    ItinerariesAdapter(List<Itinerary> dataSet)
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

        viewHolder.getView().setAsLastElement((position == dataSet.size() -1 && dataSet.size() > 3));

        viewHolder.getView().setOnViewClickListener(() ->
        {
            Toast.makeText(viewHolder.getView().getContext(), "Open detail.", Toast.LENGTH_SHORT).show();
        });

        viewHolder.getView().setOnEditButtonClickListener(() ->
        {
            Context context = viewHolder.getView().getContext();
            ObjectSharer.get().shareObject("edit_itinerary", dataSet.get(position));
            context.startActivity(new Intent(context, ItineraryCreationActivity.class));
        });

        viewHolder.getView().setOnDeleteButtonClickListener(() ->
        {
            Context context = viewHolder.getView().getContext();

            new KAlertDialog(context)
                    .setTitleText(context.getResources().getString(R.string.deleteDialogTitle))
                    .setContentText(context.getResources().getString(R.string.deleteDialogMessage))
                    .setConfirmText(context.getResources().getString(R.string.yes))
                    .setCancelText(context.getResources().getString(R.string.no))
                    .setConfirmClickListener(kAlertDialog ->
                    {
                        Itinerary itinerary = dataSet.get(position);
                        BackEndInterface.get().removeEntity(itinerary, new BackEndInterface.OnOperationCompleteListener()
                        {
                            @Override
                            public void onSuccess()
                            {
                                //  List has automatic refresh
                                Toast.makeText(context, R.string.deletedItineraryMessage, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError()
                            {
                                Toast.makeText(context, R.string.generic_error, Toast.LENGTH_SHORT).show();
                            }
                        });

                        kAlertDialog.dismissWithAnimation();
                    })
                    .show();
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
