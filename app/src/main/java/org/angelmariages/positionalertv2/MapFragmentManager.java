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

public class MapFragmentManager implements OnMapReadyCallback {
    private Activity mapFragmentActivity;
    private Marker destinationMarker;
    private Circle destinationCircle;
    private GoogleMap googleMap;

    public MapFragmentManager(Activity mapFragmentActivity) {
        this.mapFragmentActivity = mapFragmentActivity;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;

        checkPermissions();

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                onMapFragmentClick(latLng);
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });

        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                marker.hideInfoWindow();
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                if(marker.equals(destinationMarker)) {
                   destinationCircle.setCenter(marker.getPosition());
                }
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                marker.showInfoWindow();
            }
        });
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(mapFragmentActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mapFragmentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ActivityCompat.requestPermissions(mapFragmentActivity,
                            new String[]{Manifest.permission_group.LOCATION}, 0);
                }
            });
        }
    }

    private void onMapFragmentClick(LatLng latLng) {
        setDestinationMarker(latLng);
    }

    public void setDestinationMarker(LatLng latLng) {
        if(destinationMarker == null || destinationCircle == null) {
            //If not exists, add marker to map
            destinationMarker = googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Destination")
                    .draggable(true));
            //Add range circle
            destinationCircle = googleMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(500)
                    .strokeColor(Color.RED)
                    .strokeWidth(5.0f)
                    .fillColor(Color.argb(0, 0, 0, 0)));
            destinationMarker.showInfoWindow();
        } else {
            destinationMarker.setPosition(latLng);
            destinationCircle.setCenter(latLng);
        }
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

    public void updateCamera(LatLng position) {
        if(googleMap != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 13.0f));
        }
    }

    public Marker getDestinationMarker() {
        return destinationMarker;
    }
}
