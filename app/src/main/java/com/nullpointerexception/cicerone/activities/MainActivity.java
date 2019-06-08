
package com.nullpointerexception.cicerone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.google.android.material.navigation.NavigationView;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.components.AuthenticationManager;
import com.nullpointerexception.cicerone.components.ProfileImageFetcher;
import com.nullpointerexception.cicerone.components.User;
import com.nullpointerexception.cicerone.fragments.HomeFragment;
import com.nullpointerexception.cicerone.fragments.ItinerariesListFragment;
import com.nullpointerexception.cicerone.fragments.ProfileFragment;
import com.nullpointerexception.cicerone.fragments.TripsListFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
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
    private Toolbar toolbar;
    private Fragment actualFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //  Add home fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.modal_in, R.anim.modal_out);
        if(actualFragment != null)
            fragmentTransaction.remove(actualFragment);
        actualFragment = new HomeFragment();
        fragmentTransaction.add(R.id.fragmentsContainer, actualFragment).commit();

        BubbleNavigationConstraintView bottomNavigation = findViewById(R.id.bottomNavigationContainer);
        bottomNavigation.setNavigationChangeListener((view, position) ->
        {
            FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction1.setCustomAnimations(R.anim.modal_in, R.anim.modal_out);

            if(actualFragment != null)
                fragmentTransaction1.remove(actualFragment);

            switch (position)
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
        });

        User user = AuthenticationManager.get().getUserLogged();

        ProfileDrawerItem profileDrawerItem = new ProfileDrawerItem().withName(user != null? user.getDisplayName(): "")
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
                        .build()
                )
                .addDrawerItems(
                        //new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("Logout").withIdentifier(123)
                )
                .withOnDrawerItemClickListener((view, position, drawerItem) ->
                {
                    if(drawerItem.getIdentifier() == 123)
                    {
                        AuthenticationManager.get().logout();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }

                    return true;
                })
                .build();
    }

    @Override
    public void onBackPressed()
    {
        /*
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }*/
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        /*
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);*/
        return true;
    }
}