package org.angelmariages.positionalertv2;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.angelmariages.positionalertv2.destination.Destination;
import org.angelmariages.positionalertv2.destination.DestinationDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;

public class MapFragmentManager implements OnMapReadyCallback {
    private Activity mapFragmentActivity;
    private GoogleMap googleMap;

    private HashMap<Marker, Circle> destinationMarkers = new HashMap<>();
    private OnMapFragmentReady fragmentReadyListener;

    private OnDestinationChangeListener destinationChangeListener;
    private OnDescriptionMapClickListener descriptionMapClickListener;

    private boolean lite;
    private Destination destinationGroup = null;

    public MapFragmentManager(Activity mapFragmentActivity, boolean lite) {
        this.lite = lite;
        if(mapFragmentActivity instanceof OnDestinationChangeListener) {
            destinationChangeListener = (OnDestinationChangeListener) mapFragmentActivity;
        }
        this.mapFragmentActivity = mapFragmentActivity;
    }

    public MapFragmentManager(Activity mapFragmentActivity, boolean lite, Destination destinationGroup) {
        this.lite = lite;
        this.destinationGroup = destinationGroup;
        if(mapFragmentActivity instanceof OnDestinationChangeListener) {
            destinationChangeListener = (OnDestinationChangeListener) mapFragmentActivity;
        }
        if(mapFragmentActivity instanceof OnDescriptionMapClickListener) {
            descriptionMapClickListener = (OnDescriptionMapClickListener) mapFragmentActivity;
        }
        this.mapFragmentActivity = mapFragmentActivity;
    }

    public void loadMarkers(ArrayList<Destination> destinations) {
        for (Destination destination : destinations) {
            setDestinationMarker(destination);
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;

        checkPermissions();

        if(!lite) {
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    //TODO: add destination to marker and then query it on the activity with current marker
                    onMapFragmentClick(latLng);
                }
            });

            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                                       @Override
                                                       public void onInfoWindowClick(Marker marker) {
                   Destination markerDestination = (Destination) marker.getTag();
                   showMarkerDialog(markerDestination.getName(), markerDestination.getRadius(), marker);
                   }
               }
            );
        } else {
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                if (destinationGroup != null) {
                    descriptionMapClickListener.onMapClick(destinationGroup);
                }
                }
            });
        }

        if(fragmentReadyListener != null)
            fragmentReadyListener.onMapFragmentReady();

        /*googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                destinationMarkers.get(marker).setCenter(marker.getPosition());
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

            }
        });*/
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(mapFragmentActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            //TODO: check map settings
            ActivityCompat.requestPermissions(mapFragmentActivity,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            }, 0);
        }
    }

    private void onMapFragmentClick(LatLng latLng) {
        setDestinationMarker(latLng);
    }

    private Marker setDestinationMarker(Destination destination) {
        Marker tmpMarker;
        Circle tmpCircle;
        tmpMarker = googleMap.addMarker(new MarkerOptions()
                .position(destination.getdLatLng())
                .title(destination.getName()));
        tmpMarker.setTag(destination);
        tmpCircle = googleMap.addCircle(new CircleOptions()
                .center(destination.getdLatLng())
                .radius(destination.getRadius())
                .strokeColor(Color.RED)
                .strokeWidth(5.0f)
                .fillColor(Color.argb(25, 255, 0, 0)));
        tmpMarker.showInfoWindow();
        destinationMarkers.put(tmpMarker, tmpCircle);
        return tmpMarker;
    }

    private Marker setDestinationMarker(LatLng latLng) {
        Marker tmpMarker;
        Circle tmpCircle;
        tmpMarker = googleMap.addMarker(new MarkerOptions()
                        .position(latLng));
        tmpCircle = googleMap.addCircle(new CircleOptions()
                        .center(latLng)
                        .radius(500)
                        .strokeColor(Color.RED)
                        .strokeWidth(5.0f)
                        .fillColor(Color.argb(25, 255, 0, 0)));
        tmpMarker.showInfoWindow();
        showMarkerDialog(null, 0, tmpMarker);
        destinationMarkers.put(tmpMarker, tmpCircle);
        return tmpMarker;
    }

    private void showMarkerDialog(String destinationName, int radius, final Marker marker) {
        DestinationDialogFragment destinationDialogFragment = DestinationDialogFragment
                .newInstance(destinationName, radius);
        destinationDialogFragment.show(mapFragmentActivity.getFragmentManager(), "TAG");

        destinationDialogFragment.setOnDestinationDialogListener(new DestinationDialogFragment.OnDestinationDialogListener() {
            @Override
            public void onOkClicked(String destinationName, int radius) {
                Destination tmpDestination = new Destination(marker.getPosition(),
                        radius,
                        destinationName,
                        false,
                        false,
                        false);
                marker.setTitle(destinationName);
                destinationMarkers.get(marker).setRadius(radius);
                marker.setTag(tmpDestination);
                marker.showInfoWindow();

                if(destinationChangeListener != null)
                    destinationChangeListener.onAddedDestination(tmpDestination);
            }

            @Override
            public void onDeleteClicked() {
                if(destinationChangeListener != null && marker.getTag() != null)
                    destinationChangeListener.onDeletedDestination((Destination) marker.getTag());
                destinationMarkers.get(marker).remove();
                marker.remove();
            }
        });
    }

    public void setMapFragmentPadding(int left, int top, int right, int bottom) {
        int pxLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, left,
                mapFragmentActivity.getResources().getDisplayMetrics());
        int pxTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, top,
                mapFragmentActivity.getResources().getDisplayMetrics());
        int pxRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, right,
                mapFragmentActivity.getResources().getDisplayMetrics());
        int pxBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, bottom,
                mapFragmentActivity.getResources().getDisplayMetrics());
        googleMap.setPadding(pxLeft,pxTop,pxRight,pxBottom);
    }

    public Marker addMarker(LatLng position) {
        return setDestinationMarker(position);
    }

    public void updateCamera(LatLng position) {
        if(googleMap != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 13.0f));
        }
    }

    public void setCamera(LatLng position) {
        if (googleMap != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 13.0f));
        }
    }

    public interface OnMapFragmentReady {
        void onMapFragmentReady();
    }

    public void setOnMapFragmentReady(OnMapFragmentReady onMapFragmentReady) {
        if(onMapFragmentReady != null) {
            fragmentReadyListener = onMapFragmentReady;
        }
    }

    public interface OnDestinationChangeListener {
        void onDeletedDestination(Destination deletedDestination);
        void onAddedDestination(Destination addedDestination);
    }

    public interface OnDescriptionMapClickListener {
        void onMapClick(Destination destination);
    }
}
