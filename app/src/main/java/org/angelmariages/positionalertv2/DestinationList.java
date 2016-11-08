package org.angelmariages.positionalertv2;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DestinationList extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    private ArrayList<Destination> destinations;
    private ExpandableListView destinationsListView;

    public DestinationList() {
        // Required empty public constructor
    }

    public static DestinationList newInstance(ArrayList<Destination> destinations) {
        DestinationList fragment = new DestinationList();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, destinations);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.sendLog(String.valueOf(getArguments().size()));
        if (getArguments() != null) {
            destinations = (ArrayList<Destination>) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_destination_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        destinationsListView = (ExpandableListView) view.findViewById(R.id.destinationsListView);

        SimpleExpandableListAdapter simpleExpandableListAdapter = new SimpleExpandableListAdapter(
                view.getContext(),
                getDestinationsAsGroup(),
                android.R.layout.simple_expandable_list_item_1,
                new String[]{"NAME"},
                new int[]{android.R.id.text1},
                getDestinationsGroupData(),
                android.R.layout.simple_expandable_list_item_2,
                new String[]{"RADIUS", "LATITUDE"},
                new int[]{android.R.id.text1, android.R.id.text2}
                );

        destinationsListView.setAdapter(simpleExpandableListAdapter);
    }

    private List<HashMap<String, String>> getDestinationsAsGroup() {
        List<HashMap<String, String>> tmpGroups = new ArrayList<>();

        for (final Destination destination : destinations) {
            tmpGroups.add(new HashMap<String, String>(){{
              put("NAME", destination.getName());
            }});
        }

        return tmpGroups;
    }

    private List<ArrayList<Map<String,String>>> getDestinationsGroupData() {
        List<ArrayList<Map<String,String>>> tmpGroupsData = new ArrayList<>();

        for (final Destination destination : destinations) {
            tmpGroupsData.add(new ArrayList<Map<String, String>>(){{
                add(new HashMap<String, String>(){{
                    put("RADIUS", String.valueOf(destination.getRadius()));
                    put("LATITUDE", String.valueOf(destination.getLatitude()));
                    put("LONGITUDE", String.valueOf(destination.getLongitude()));
                    put("ACTIVE", "FALSE");
                }});
            }});
        }

        return tmpGroupsData;
    }
}
