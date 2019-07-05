
package com.nullpointerexception.cicerone.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.components.AlarmReceiver;
import com.nullpointerexception.cicerone.components.AuthenticationManager;
import com.nullpointerexception.cicerone.components.BackEndInterface;
import com.nullpointerexception.cicerone.components.Itinerary;
import com.nullpointerexception.cicerone.components.ObjectSharer;
import com.nullpointerexception.cicerone.components.ProfileImageFetcher;
import com.nullpointerexception.cicerone.components.User;
import com.nullpointerexception.cicerone.fragments.HomeFragment;
import com.nullpointerexception.cicerone.fragments.ItinerariesListFragment;
import com.nullpointerexception.cicerone.fragments.ProfileFragment;
import com.nullpointerexception.cicerone.fragments.TripsListFragment;

/**
 * TODO: Documenta
 */
public class MainActivity extends AppCompatActivity
{

    /*
            Constants
     */
    private final int BOTTOM_NAVIGATION_HOME = 0;
    private final int BOTTOM_NAVIGATION_TRIPS = 1;
    private final int BOTTOM_NAVIGATION_ITINERARIES = 2;
    private final int BOTTOM_NAVIGATION_PROFILE = 3;

    /*
            Vars
     */
    private BubbleNavigationConstraintView bottomNavigation;
    private Toolbar toolbar;
    private Fragment actualFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
                Enable service
         */
        SharedPreferences sharedPreferences = getSharedPreferences("notificationsListener", MODE_PRIVATE);
        String id = AuthenticationManager.get().getUserLogged().getId();
        sharedPreferences.edit().putString("idUser", id).apply();
        AlarmReceiver.setAlarm(getApplicationContext(), false);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //  Add home fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.modal_in, R.anim.modal_out);
        if(actualFragment != null)
            fragmentTransaction.remove(actualFragment);
        actualFragment = new HomeFragment();
        fragmentTransaction.add(R.id.fragmentsContainer, actualFragment).commit();

        bottomNavigation = findViewById(R.id.bottomNavigationContainer);
        bottomNavigation.setNavigationChangeListener((view, position) -> showSection(position));

        refreshMenu();

        if(getIntent().hasExtra("notification_info"))
        {
            Log.i("Notifiche", "MainActivity -> checking extra");

            Itinerary itinerary = new Itinerary();
            itinerary.setId(getIntent().getExtras().getString("notification_info"));
            itinerary.getFieldsFromId();

            BackEndInterface.get().getEntity(itinerary, new BackEndInterface.OnOperationCompleteListener()
            {
                @Override
                public void onSuccess()
                {
                    //  If itinerary has been created by this user
                    if(itinerary.getCicerone().getId().equals(AuthenticationManager.get().getUserLogged().getId()))
                    {
                        showSection(BOTTOM_NAVIGATION_ITINERARIES);
                        bottomNavigation.setCurrentActiveItem(BOTTOM_NAVIGATION_ITINERARIES);
                        ObjectSharer.get().shareObject("show_trip_as_cicerone", itinerary);
                    }
                    else
                    {
                        showSection(BOTTOM_NAVIGATION_TRIPS);
                        bottomNavigation.setCurrentActiveItem(BOTTOM_NAVIGATION_TRIPS);
                        ObjectSharer.get().shareObject("show_trip_as_user", itinerary);
                    }
                }

                @Override
                public void onError() { }
            });
        }
    }

    @Override
    protected void onResume()
    {
        if(ObjectSharer.get().getSharedObject("user_changed") != null &&
                ObjectSharer.get().getSharedObject("user_changed").equals("true"))
        {
            refreshMenu();
            ObjectSharer.get().shareObject("user_changed", "");
        }

        super.onResume();
    }

    private void showSection(int index)
    {
        FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
        fragmentTransaction1.setCustomAnimations(R.anim.modal_in, R.anim.modal_out);

        if(actualFragment != null)
            fragmentTransaction1.remove(actualFragment);

        switch (index)
        {
            default:
            case BOTTOM_NAVIGATION_HOME:
                actualFragment = new HomeFragment();
                break;
            case BOTTOM_NAVIGATION_TRIPS:
                actualFragment = new TripsListFragment();
                break;
            case BOTTOM_NAVIGATION_ITINERARIES:
                actualFragment = new ItinerariesListFragment();
                break;
            case BOTTOM_NAVIGATION_PROFILE:
                actualFragment = new ProfileFragment();
                break;
        }

        fragmentTransaction1.add(R.id.fragmentsContainer, actualFragment).commit();
    }

    private void refreshMenu()
    {
        User user = AuthenticationManager.get().getUserLogged();

        ProfileDrawerItem profileDrawerItem = new ProfileDrawerItem()
                .withName(user != null? user.getDisplayName(): "")
                .withEmail(user != null? user.getEmail(): "");

        if (user != null)
        {
            new ProfileImageFetcher(getApplicationContext())
                    .fetchImageOf(user, drawable ->
                    {
                        profileDrawerItem.withIcon(drawable);
                        drawMenuWith(profileDrawerItem);
                    });
        }

        drawMenuWith(profileDrawerItem);
    }

    public void drawMenuWith(ProfileDrawerItem profileDrawerItem)
    {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        boolean developerMode = sharedPreferences.getBoolean("developer_enabled", false);

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(new AccountHeaderBuilder()
                        .withActivity(this)
                        .addProfiles
                                (
                                        profileDrawerItem
                                )
                        .withSelectionListEnabled(false)
                        .withOnAccountHeaderListener((view, profile, current) ->
                        {
                            if( ! (actualFragment instanceof ProfileFragment))
                            {
                                bottomNavigation.setCurrentActiveItem(BOTTOM_NAVIGATION_PROFILE);
                                showSection(BOTTOM_NAVIGATION_PROFILE);
                            }
                            return false;
                        })
                        .build()
                )
                .addDrawerItems(
                        //new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("Logout").withIdentifier(123),
                        developerMode ?
                        new SecondaryDrawerItem().withName("Developer tools")
                                .withOnDrawerItemClickListener((view, position, drawerItem) ->
                                {
                                    startActivity(new Intent(MainActivity.this, DevelopersToolsActivity.class));
                                    return false;
                                })
                        : new DividerDrawerItem()

                )
                .withOnDrawerItemClickListener((view, position, drawerItem) ->
                {
                    if(drawerItem.getIdentifier() == 123)   //  Logout
                    {
                        AuthenticationManager.get().logout();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        sharedPreferences.edit().putString("idUser", null).apply();
                        finish();
                    }

                    return true;
                })
                .build();
    }
}