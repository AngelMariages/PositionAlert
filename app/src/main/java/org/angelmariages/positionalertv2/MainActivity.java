package org.angelmariages.positionalertv2;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.angelmariages.positionalertv2.destination.Destination;
import org.angelmariages.positionalertv2.destination.DestinationDBHelper;
import org.angelmariages.positionalertv2.destination.DestinationManager;
import org.angelmariages.positionalertv2.destination.destinationList.DestinationList;
import org.angelmariages.positionalertv2.destination.destinationInterfaces.DestinationChangeListener;
import org.angelmariages.positionalertv2.destination.destinationInterfaces.DestinationToDBListener;
import org.angelmariages.positionalertv2.userManager.AuthManager;
import org.angelmariages.positionalertv2.userManager.DownloadUserImage;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements DestinationChangeListener, MapFragmentManager.OnDescriptionMapClickListener {

    private MapFragmentManager mapFragmentManager;
    private MapFragment mapFragment;

    private DatabaseReference firebaseRef;

    private DestinationManager destinationManager;
    private DestinationDBHelper dbHelper;
    private NavigationView navigationView;
    private DestinationToDBListener mToDBListener;
    private String uniqueID = "non set";

    private AuthManager mAuthManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DestinationDBHelper(this.getApplicationContext());
        FirebaseDatabase firebaseDatabase = U.getFirebaseDatabase();
        firebaseRef = firebaseDatabase.getReference();

        uniqueID = FirebaseInstanceId.getInstance().getId();

        Intent intent = getIntent();
        if (intent.hasExtra(U.RINGTONE_TO_ACTIVITY)) {
            String ringtone = intent.getStringExtra(U.RINGTONE_TO_ACTIVITY);
            loadStopAlarmFragment(ringtone);

        } else {
            loadMapFragment();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        destinationManager = new DestinationManager(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        NavigationView.OnNavigationItemSelectedListener itemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

                if (id == R.id.nav_map) {
                    loadMapFragment();
                } else if (id == R.id.nav_destinationsList) {
                    loadDestinationsListFragment();
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        };

        navigationView.setNavigationItemSelectedListener(itemSelectedListener);

        if(navigationView != null) {
            navigationView.getMenu().getItem(0).setChecked(true);
            //--
            MenuItem activeMode = navigationView.getMenu().findItem(R.id.activeModeItem);
            Switch activeModeSwitch = (Switch) MenuItemCompat.getActionView(activeMode);
            activeModeSwitch.setChecked(U.isInActiveMode(this.getApplicationContext()));
            activeModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    setActiveMode(b);
                }
            });
        }

        //--
        mAuthManager = new AuthManager(this);
        mAuthManager.setSuccesfulLoginListener(new AuthManager.OnSuccesfulLoginListener() {
            @Override
            public void succesfulLogin(String username, String email, Uri image) {
                navigationView.removeHeaderView(navigationView.getHeaderView(0));
                navigationView.inflateHeaderView(R.layout.nav_header_main);
                View headerView = navigationView.getHeaderView(0);
                TextView userName = (TextView) headerView.findViewById(R.id.userNameText);
                TextView userEmail = (TextView) headerView.findViewById(R.id.userEmailText);
                ImageView userImage = (ImageView) headerView.findViewById(R.id.userImageView);
                ImageButton logoutButton = (ImageButton) navigationView.getHeaderView(0).findViewById(R.id.buttonLogout);

                userName.setText(username);
                userEmail.setText(email);

                logoutButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAuthManager.signOut();
                    }
                });

                new DownloadUserImage(userImage, MainActivity.this.getApplicationContext())
                        .execute(image.toString());
            }
        });
        mAuthManager.setOnLogoutListener(new AuthManager.LogoutListener() {
            @Override
            public void onLogout() {
                navigationView.removeHeaderView(navigationView.getHeaderView(0));
                navigationView.inflateHeaderView(R.layout.nav_header_loggedout);
                View headerView = navigationView.getHeaderView(0);
                final Button loginButton = (Button) headerView.findViewById(R.id.buttonLogin);
                final ProgressBar progressBar = (ProgressBar) headerView.findViewById(R.id.loginProgressBar);

                loginButton.setEnabled(true);
                loginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loginButton.setEnabled(false);
                        progressBar.setVisibility(View.VISIBLE);
                        mAuthManager.signIn();
                    }
                });
            }
        });
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        destinationManager.disconnectApiClient();
        mAuthManager.removeListener();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sound_select) {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select ringtone for alarm:");
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);

            String ringtoneSaved = getSharedPreferences(U.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                    .getString(U.RINGTONE_PREFERENCE, null);
            if(ringtoneSaved != null)
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(ringtoneSaved));

            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
            startActivityForResult(intent, U.RINGTONE_SELECT_RESULT);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == U.RINGTONE_SELECT_RESULT && resultCode == RESULT_OK) {
            Uri ringtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            Ringtone ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
            U.showSToast("Ringtone selected: " + ringtone.getTitle(this), this);
            getSharedPreferences(U.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
                    .putString(U.RINGTONE_PREFERENCE, ringtoneUri.toString())
                    .apply();
        } else if(requestCode == 1) {
            if(mAuthManager != null) {
                mAuthManager.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void loadStopAlarmFragment(String ringtone) {
        getFragmentManager().beginTransaction().replace(R.id.main_fragment,
                StopAlarmFragment.newInstance(ringtone)
                ).commit();
    }

    private void loadMapFragment() {
        loadMapFragment(null, 0);
    }

    private void loadMapFragment(final LatLng camera, final int radius) {
        mapFragment = MapFragment.newInstance();

        getFragmentManager().beginTransaction().replace(R.id.main_fragment, mapFragment).commit();

        mapFragmentManager = new MapFragmentManager(this);

        mapFragment.getMapAsync(mapFragmentManager);

        mapFragmentManager.setOnMapFragmentReady(new MapFragmentManager.OnMapFragmentReady() {
            @Override
            public void onMapFragmentReady() {
                mapFragmentManager.loadMarkers(getDestinationsFromDB());
                if (camera != null) {
                    mapFragmentManager.updateCamera(camera, radius);
                }
            }
        });

        if(navigationView != null) {
            navigationView.getMenu().getItem(0).setChecked(true);
        }
    }

    private ArrayList<Destination> getDestinationsFromDB() {
        return dbHelper.getAllDestinations();
    }

    private void loadDestinationsListFragment() {
        getFragmentManager().beginTransaction().replace(R.id.main_fragment,
                DestinationList.newInstance(getDestinationsFromDB())).commit();

        mapFragment = null;
        mapFragmentManager = null;
    }

    private void setActiveMode(boolean active) {
        if(active) {
            if (!U.isInActiveMode(this.getApplicationContext()))
                startService(new Intent(getBaseContext(), ActiveModeService.class));
        } else {
            if(U.isInActiveMode(this.getApplicationContext()))
                stopService(new Intent(getBaseContext(), ActiveModeService.class));
        }
        getSharedPreferences(U.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
                .putBoolean(U.ACTIVE_MODE_PREFERENCE, active)
                .apply();
    }

    /** START overrided methods of DestinationChangeListener */
    @Override
    public void onAdded(Destination addedDestination) {
        mToDBListener.onDestinationAdded((int) dbHelper.insertDestination(addedDestination));
        destinationManager.addDestination(addedDestination);

        DatabaseReference push = firebaseRef.child("users")
                .child(mAuthManager.getUserUID() != null ? mAuthManager.getUserUID() : uniqueID)
                .child("destinations")
                .push();
        push.setValue(addedDestination);
    }

    @Override
    public void onMoved(int destinationID, LatLng newPosition) {
        dbHelper.updateLatLng(destinationID, newPosition);
        Destination deleted = dbHelper.getDestination(destinationID);
        destinationManager.removeDestination(deleted.generateID());
        deleted.setLatLng(newPosition);
        destinationManager.addDestination(deleted);
    }

    @Override
    public void onActiveChanged(int destinationID, boolean active) {
        dbHelper.updateValue(destinationID, DestinationDBHelper.COLUMN_ACTIVE, active);
        Destination tmpDestination = dbHelper.getDestination(destinationID);
        if(active)
            destinationManager.addDestination(tmpDestination);
        else
            destinationManager.removeDestination(tmpDestination.generateID());
    }

    @Override
    public void onDeleteOnReachChanged(int destinationID, boolean deleteOnReach) {
        Destination tmpDestination = dbHelper.getDestination(destinationID);
        destinationManager.removeDestination(tmpDestination.generateID());
        dbHelper.updateValue(destinationID, DestinationDBHelper.COLUMN_DELETEONREACH, deleteOnReach);
        tmpDestination.setDeleteOnReach(deleteOnReach);
        destinationManager.addDestination(tmpDestination);
        U.showSToast(deleteOnReach ? "Destination will be deleted when reached" : "Destination will be kept when reached", this);
    }

    @Override
    public void onChanged(Destination changedDestination) {
        dbHelper.updateDestination(changedDestination.getDatabaseID(), changedDestination);
        Destination tmpDestination = dbHelper.getDestination(changedDestination.getDatabaseID());
        destinationManager.removeDestination(tmpDestination.generateID());
        destinationManager.addDestination(tmpDestination);
    }

    @Override
    public void onDeleted(int destinationID) {
        Destination deleted = dbHelper.getDestination(destinationID);
        destinationManager.removeDestination(deleted.generateID());
        dbHelper.deleteDestination(destinationID);
    }
    /** END overrided methods of DestinationChangeListener */

    @Override
    public void onMapDescriptionClick(Destination destination) {
        loadMapFragment(destination.getdLatLng(), destination.getRadius());
    }

    public void setDestinationToDBListener(DestinationToDBListener destinationToDBListener) {
        if(destinationToDBListener != null) {
            mToDBListener = destinationToDBListener;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            //@TODO change with another code, imrpove all permission request
            case 0: {
                mapFragmentManager.setMapParameters();
            } break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    public interface OnActivityResultListener {
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }
}
