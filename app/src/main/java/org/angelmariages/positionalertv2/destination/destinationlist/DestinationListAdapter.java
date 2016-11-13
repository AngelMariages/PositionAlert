package org.angelmariages.positionalertv2.destination.destinationlist;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;

import org.angelmariages.positionalertv2.MapFragmentManager;
import org.angelmariages.positionalertv2.Utils;
import org.angelmariages.positionalertv2.destination.Destination;
import org.angelmariages.positionalertv2.R;

import java.util.ArrayList;

public class DestinationListAdapter extends BaseExpandableListAdapter {
    private final Context context;
    private final ArrayList<Destination> destinations;
    private final int groupLayout;
    private final int childLayout;

    private OnDestinationEditListener mListener;

    public DestinationListAdapter(Context context, ArrayList<Destination> destinations) {
        if(context instanceof OnDestinationEditListener) {
            mListener = (OnDestinationEditListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDestinationEditListener");
        }
        this.context = context;
        this.destinations = destinations;
        this.groupLayout = R.layout.destination_list_item_title;
        this.childLayout = R.layout.destination_list_item_description;
    }

    @Override
    public int getGroupCount() {
        return destinations.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Object getGroup(int i) {
        return destinations.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return destinations.get(i);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(groupLayout, null);
        }

        if (convertView != null) {
            TextView titleText = (TextView) convertView.findViewById(R.id.destinationText);
            titleText.setText(destinations.get(groupPosition).getName());
        }

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(childLayout, null);
        }

        if (convertView != null) {
            TextView radiusText = (TextView) convertView.findViewById(R.id.radiusText);
            TextView latitudeText = (TextView) convertView.findViewById(R.id.latitudeText);
            TextView longitudeText = (TextView) convertView.findViewById(R.id.longitudeText);
            Switch activeSwitch = (Switch) convertView.findViewById(R.id.activeSwitch);
            Switch deleteOnReachSwitch = (Switch) convertView.findViewById(R.id.deleteOnReach);
            Button deleteButton = (Button) convertView.findViewById(R.id.destinationDeleteButton);

            final Destination current = destinations.get(groupPosition);

            radiusText.setText(String.format(context.getString(R.string.radiusText), current.getRadius()));
            latitudeText.setText(String.format(context.getString(R.string.latitudeText), current.getLatitude()));
            longitudeText.setText(String.format(context.getString(R.string.longitudeText), current.getLongitude()));

            final CompoundButton.OnCheckedChangeListener activeSwitchListener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
                    if (mListener != null) {
                        mListener.onActiveChanged(current.getDatabaseID(), check);
                        current.setActivated(check);
                    }
                }
            };

            final CompoundButton.OnCheckedChangeListener deleteOnReachListener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
                    if (mListener != null) {
                        mListener.onDeleteOnReachChanged(current.getDatabaseID(), check);
                        current.setDeleteOnReach(check);
                    }
                }
            };

            activeSwitch.setOnCheckedChangeListener(null);
            deleteOnReachSwitch.setOnCheckedChangeListener(null);

            activeSwitch.setChecked(current.active());
            deleteOnReachSwitch.setChecked(current.removeOnReach());

            activeSwitch.setOnCheckedChangeListener(activeSwitchListener);

            deleteOnReachSwitch.setOnCheckedChangeListener(deleteOnReachListener);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onDelete(current.getDatabaseID());
                        destinations.remove(groupPosition);
                        notifyDataSetChanged();
                    }
                }
            });

            loadMapFragment((Activity) convertView.getContext(), groupPosition);
        }
        return convertView;
    }

    private void loadMapFragment(Activity activity, final int groupPosition) {
        GoogleMapOptions googleMapOptions = new GoogleMapOptions();
        googleMapOptions.liteMode(true);
        googleMapOptions.camera(new CameraPosition.Builder()
                .target(destinations.get(groupPosition).getdLatLng())
                .zoom(Utils.getZoomByRadius(destinations.get(groupPosition).getRadius()))
                .build());
        final MapFragment mapFragment = MapFragment.newInstance(googleMapOptions);

        activity.getFragmentManager().beginTransaction()
                .replace(R.id.destinationDescriptionMap, mapFragment).commit();

        final MapFragmentManager mapFragmentManager = new MapFragmentManager(activity, destinations.get(groupPosition));

        mapFragment.getMapAsync(mapFragmentManager);

        mapFragmentManager.setOnMapFragmentReady(new MapFragmentManager.OnMapFragmentReady() {
            @Override
            public void onMapFragmentReady() {
                mapFragmentManager.addMarker(destinations.get(groupPosition));
            }
        });
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    public void setOnDestinationEditListener(OnDestinationEditListener onDestinationEditListener) {
        mListener = onDestinationEditListener;
    }

    public interface OnDestinationEditListener {
        void onActiveChanged(int destinationID, boolean active);
        void onDeleteOnReachChanged(int destinationID, boolean deleteOnReach);
        void onDelete(int destinationID);
    }
}
