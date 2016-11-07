package org.angelmariages.positionalertv2;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.MapFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MapFragmentManager.OnDestinationChangeListener {

    public MapFragmentManager mapFragmentManager;
    private MapFragment mapFragment;

    private DestinationManager destinationManager;
    private final DestinationDBHelper dbHelper = new DestinationDBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadMapFragment();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);//Afegir Toolbar de dalt
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);//Boto flotant

        destinationManager = new DestinationManager(this.getApplicationContext());

        openOrCreateDatabase("MyDestinations.db", MODE_PRIVATE, null);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /**
                    @TODO: add snackbar when removing the fence
                    mapFragmentManager.setMapFragmentPadding(0, 0, 0, 48);
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mapFragmentManager.setMapFragmentPadding(0, 0, 0, 0);
                                }
                            }).show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mapFragmentManager.setMapFragmentPadding(0, 0, 0, 0);
                        }
                    }, 2900);
                */
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);//Afegir panell de l'esquerra de nav
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        //destinationManager.removeDestination(defaultDestination.getName());
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sound_select) {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select ringtone for alarm:");
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);

            String ringtoneSaved = getSharedPreferences(Utils.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                    .getString(Utils.RINGTONE_PREFERENCE, null);
            if(ringtoneSaved != null)
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(ringtoneSaved));

            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
            startActivityForResult(intent, Utils.RINGTONE_SELECT_RESULT);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Utils.RINGTONE_SELECT_RESULT && resultCode == RESULT_OK) {
            Uri ringtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            Ringtone ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
            Utils.showSToast("Ringtone selected: " + ringtone.getTitle(this), this);
            getSharedPreferences(Utils.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
                    .putString(Utils.RINGTONE_PREFERENCE, ringtoneUri.toString())
                    .apply();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            loadMapFragment();
        } else if (id == R.id.nav_gallery) {
            removeMapFragment();
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadMapFragment() {
        //mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.main_fragment);//Afegir mapa
        if(mapFragment == null) {
            mapFragment = MapFragment.newInstance();
        }
        getFragmentManager().beginTransaction().replace(R.id.main_fragment, mapFragment).commit();

        mapFragmentManager = new MapFragmentManager(this);

        mapFragment.getMapAsync(mapFragmentManager);

        mapFragmentManager.setOnMapFragmentReady(new MapFragmentManager.OnMapFragmentReady() {
            @Override
            public void onMapFragmentReady() {
                mapFragmentManager.loadMarkers(getDestinationsFromDB());
            }
        });
    }

    private ArrayList<Destination> getDestinationsFromDB() {
        return dbHelper.getAllDestinations();
    }

    public void removeMapFragment() {
        getFragmentManager().beginTransaction().remove(mapFragment).commit();
    }

    @Override
    public void onDeletedDestination(Destination deletedDestination) {
        dbHelper.deleteDestination(deletedDestination.getName());
    }

    @Override
    public void onAddedDestination(Destination addedDestination) {
        dbHelper.insertDestination(addedDestination);
    }
}
