package org.angelmariages.positionalertv2.destination.destinationInterfaces;

import com.google.android.gms.maps.model.LatLng;
import org.angelmariages.positionalertv2.destination.Destination;

public interface DestinationChangeListener {
    void onDeleted(int destinationID);
    void onAdded(Destination addedDestination);
    void onMoved(int destinationID, LatLng newPosition);
    void onActiveChanged(int destinationID, boolean active);
    void onDeleteOnReachChanged(int destinationID, boolean deleteOnReach);
    void onChanged(Destination destination);
}
