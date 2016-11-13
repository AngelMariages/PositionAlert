package org.angelmariages.positionalertv2.destination.destinationlist;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import org.angelmariages.positionalertv2.destination.Destination;
import org.angelmariages.positionalertv2.R;
import org.angelmariages.positionalertv2.Utils;

import java.util.ArrayList;

public class DestinationList extends Fragment {
    private int lastExpandedGroupId = -1;
    private static final String ARG_DESTINATIONS = "destinationsList";

    private ArrayList<Destination> destinations;

    public DestinationList() {
        // Required empty public constructor
    }

    public static DestinationList newInstance(ArrayList<Destination> destinations) {
        DestinationList fragment = new DestinationList();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DESTINATIONS, destinations);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            destinations = (ArrayList<Destination>) getArguments().getSerializable(ARG_DESTINATIONS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_destination_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final ExpandableListView destinationsListView = (ExpandableListView) view.findViewById(R.id.destinationsListView);

        setIndicatorBounds(view, destinationsListView);

        DestinationListAdapter destinationListAdapter = new DestinationListAdapter(view.getContext(),
                destinations
        );

        destinationsListView.setAdapter(destinationListAdapter);

        destinationsListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if(lastExpandedGroupId != -1 && groupPosition != lastExpandedGroupId) {
                    destinationsListView.collapseGroup(lastExpandedGroupId);
                }

                lastExpandedGroupId = groupPosition;
            }
        });
    }

    private void setIndicatorBounds(View view, ExpandableListView expandableListView) {
        DisplayMetrics dm = new DisplayMetrics();
        Activity act = (Activity) view.getContext();
        act.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        expandableListView.setIndicatorBounds(width - Utils.getDipsFromPixels(35, expandableListView),
                width - Utils.getDipsFromPixels(5, expandableListView));
    }
}
