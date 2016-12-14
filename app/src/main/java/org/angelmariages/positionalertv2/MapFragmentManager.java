package org.angelmariages.positionalertv2;

import android.app.Activity;
import android.graphics.Color;
import android.util.TypedValue;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.angelmariages.positionalertv2.destination.Destination;
import org.angelmariages.positionalertv2.destination.DestinationDialogFragment;
import org.angelmariages.positionalertv2.destination.destinationInterfaces.DestinationChangeListener;
import org.angelmariages.positionalertv2.destination.destinationInterfaces.DestinationToDBListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MapFragmentManager implements OnMapReadyCallback {
    private final Activity mapFragmentActivity;
    private GoogleMap googleMap;

    private final HashMap<Marker, Circle> circlesByMarker = new HashMap<>();
    private OnMapFragmentReady fragmentReadyListener;

    private DestinationChangeListener destinationChangeListener;
    private OnDescriptionMapClickListener descriptionMapClickListener;

    private final boolean lite;
    private Destination destinationGroup = null;

    public MapFragmentManager(Activity mapFragmentActivity) {
        this.lite = false;
        if(mapFragmentActivity instanceof DestinationChangeListener) {
            destinationChangeListener = (DestinationChangeListener) mapFragmentActivity;
        }
        this.mapFragmentActivity = mapFragmentActivity;
    }

    public MapFragmentManager(Activity mapFragmentActivity, Destination destinationGroup) {
        this.lite = true;
        this.destinationGroup = destinationGroup;
        if(mapFragmentActivity instanceof DestinationChangeListener) {
            destinationChangeListener = (DestinationChangeListener) mapFragmentActivity;
        }
        if(mapFragmentActivity instanceof OnDescriptionMapClickListener) {
            descriptionMapClickListener = (OnDescriptionMapClickListener) mapFragmentActivity;
        }
        this.mapFragmentActivity = mapFragmentActivity;
    }

    public void loadMarkers(ArrayList<Destination> destinations) {
        for(Destination destination : destinations) {
            setDestinationMarker(destination, false);
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
                    onMapFragmentClick(latLng);
                }
            });

            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                   @Override
                   public void onInfoWindowClick(Marker marker) {
                       Destination markerDestination = (Destination) marker.getTag();
                       showMarkerDialog(markerDestination, marker);
                   }
               }
            );

            googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

                @Override
                public void onMarkerDragStart(Marker marker) {
                }

                @Override
                public void onMarkerDrag(Marker marker) {
                    circlesByMarker.get(marker).setCenter(marker.getPosition());
                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    if(destinationChangeListener != null)
                        destinationChangeListener.onMoved(((Destination)marker.getTag()).getDatabaseID(), marker.getPosition());
                }
            });
        } else {
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if(destinationGroup != null) {
                        descriptionMapClickListener.onMapDescriptionClick(destinationGroup);
                    }
                }
            });

            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    if(destinationGroup != null) {
                        descriptionMapClickListener.onMapDescriptionClick(destinationGroup);
                    }
                }
            });
        }

        if(fragmentReadyListener != null)
            fragmentReadyListener.onMapFragmentReady();
    }

    private void checkPermissions() {
        if(!U.checkPositionPermissions(mapFragmentActivity)) {
            U.askPositionPermissions(mapFragmentActivity);
        } else {
            setMapParameters();
        }
    }

    public void setMapParameters() {
        if(googleMap != null) {
            try {
                googleMap.setMyLocationEnabled(true);
            } catch (SecurityException e) {
                checkPermissions();
            }
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
        }
    }

    private void onMapFragmentClick(LatLng latLng) {
        setDestinationMarker(latLng);
    }

    private Marker setDestinationMarker(Destination destination, boolean showInfoWindow) {
        Marker tmpMarker;
        Circle tmpCircle;
        tmpMarker = googleMap.addMarker(new MarkerOptions()
                .position(destination.getdLatLng())
                .draggable(true)
                .title(destination.getName())
                .snippet("active"));
        tmpMarker.setTag(destination);
        tmpMarker.setAlpha(destination.active() ? 1.0f : 0.5f);
        BitmapDescriptor bitmap = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);
        tmpMarker.setIcon(bitmap);
        if(showInfoWindow) {
            tmpMarker.showInfoWindow();
        }
        tmpCircle = googleMap.addCircle(new CircleOptions()
                .center(destination.getdLatLng())
                .radius(destination.getRadius())
                .strokeColor(Color.argb(destination.active() ? 255 : 75, 255, 0, 0))
                .strokeWidth(5.0f)
                .fillColor(Color.argb(25, 255, 0, 0)));
        circlesByMarker.put(tmpMarker, tmpCircle);
        return tmpMarker;
    }

    private Marker setDestinationMarker(LatLng latLng) {
        Marker tmpMarker;
        Circle tmpCircle;
        tmpMarker = googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true));
        BitmapDescriptor bitmap = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);
        tmpMarker.setIcon(bitmap);
        tmpCircle = googleMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(500)
                .strokeColor(Color.RED)
                .strokeWidth(5.0f)
                .fillColor(Color.argb(25, 255, 0, 0)));
        showMarkerDialog(null, tmpMarker);
        circlesByMarker.put(tmpMarker, tmpCircle);
        return tmpMarker;
    }

    private void showMarkerDialog(Destination destination, final Marker marker) {
        if(destination == null) {
            destination = new Destination(U.NULL_ID,
                    null,
                    marker.getPosition(),
                    500,
                    true,
                    false,
                    false);
        }

        DestinationDialogFragment destinationDialogFragment = DestinationDialogFragment
                .newInstance(destination);
        destinationDialogFragment.show(mapFragmentActivity.getFragmentManager(), "TAG");
        final MainActivity mainActivity = (MainActivity) mapFragmentActivity;

        destinationDialogFragment.setOnDestinationDialogListener(new DestinationDialogFragment.DestinationDialogListener() {
            @Override
            public void onOkClicked(final Destination destination) {
                marker.setTitle(destination.getName());
                circlesByMarker.get(marker).setRadius(destination.getRadius());
                marker.showInfoWindow();
                if(destination.getDatabaseID() == U.NULL_ID) {
                    mainActivity.setDestinationToDBListener(new DestinationToDBListener() {
                        @Override
                        public void onDestinationAdded(int destinationID) {
                            destination.setDatabaseID(destinationID);
                            marker.setTag(destination);
                            U.showLToast("Destination added succesfully!", mainActivity);
                        }
                    });

                    if (destinationChangeListener != null)
                        destinationChangeListener.onAdded(destination);
                } else {
                    if(destinationChangeListener != null)
                        destinationChangeListener.onChanged(destination);

                    U.showLToast("Destination changed succesfully!", mainActivity);
                }
            }

            @Override
            public void onDeleteClicked() {
                if(destinationChangeListener != null && marker.getTag() != null)
                    destinationChangeListener.onDeleted(((Destination)marker.getTag()).getDatabaseID());
                circlesByMarker.get(marker).remove();
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
        googleMap.setPadding(pxLeft, pxTop, pxRight, pxBottom);
    }

    public void addMarker(Destination destination) {
        setDestinationMarker(destination, true);
    }

    public void updateCamera(LatLng position, int radius) {
        if(googleMap != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, U.getZoomByRadius(radius, position.latitude) - 0.5f));
        }
    }

    public void setCamera(LatLng position) {
        if(googleMap != null) {
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

    public interface OnDescriptionMapClickListener {
        void onMapDescriptionClick(Destination destination);
    }
}
