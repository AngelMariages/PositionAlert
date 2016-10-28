/**
package xyz.cesarbiker.positionalertv2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class _MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MapFragmentManager mapFragmentManager;
    private Location firstLocation;
    private Context mainContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);//Afegir Toolbar de dalt
        setSupportActionBar(toolbar);

        mainContext = this.getApplicationContext();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);//Boto flotant
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapFragmentManager.setMapFragmentPadding(0, 0, 0, 48);
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getLocationForZoomIn();
                            }
                        }).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mapFragmentManager.setMapFragmentPadding(0, 0, 0, 0);
                    }
                }, 2900);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);//Afegir panell de l'esquerra de nav
        navigationView.setNavigationItemSelectedListener(this);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);//Afegir mapa
        mapFragmentManager = new MapFragmentManager(mainContext);
        mapFragment.getMapAsync(mapFragmentManager);

        //TODO: Show a helptip like 'Where do you want yo go?' with a toast

        getLocationForZoomIn();
    }

    private void getLocationForZoomIn() {
        final LocationApiClient locationApiClient = new LocationApiClient(this);
        locationApiClient.connectToApi();

        new CountDownTimer(5000, 1000) {
            boolean gotLocation = false;

            @Override
            public void onTick(long millisUntilFinished) {
                //TODO: Update language
                if(!gotLocation) {
                    Utils.showToast("Getting current position...", Toast.LENGTH_SHORT, mainContext);
                    Utils.sendLog("getLocationForZoom: Getting location.");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for Activity#requestPermissions for more details.
                            return;
                        }
                    }
                    firstLocation = LocationServices.FusedLocationApi.getLastLocation(locationApiClient.getGoogleApiClient());
                    if(firstLocation != null) {
                        gotLocation = true;
                        Utils.sendLog("getLocationForZoom: Got location.");
                        //Zoom in the map
                        mapFragmentManager.updateCamera(new LatLng(firstLocation.getLatitude(),
                                firstLocation.getLongitude()));
                        locationApiClient.disconnectFromApi();
                    }
                }
            }

            @Override
            public void onFinish() {
                if(!gotLocation) {
                    start();
                    Utils.sendLog("getLocationForZoom: Couldn't get location, restarting...");
                }
            }
        }.start();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
*/
