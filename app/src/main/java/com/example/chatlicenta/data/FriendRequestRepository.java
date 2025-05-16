package com.example.chatlicenta.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.chatlicenta.data.model.FriendRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Simulează un back‑end local pentru cereri de prietenie.
 */
public class FriendRequestRepository {

    // Ține lista curentă de cereri
    private final MutableLiveData<List<FriendRequest>> data =
            new MutableLiveData<>(new ArrayList<>());

    /** Exposează LiveData pentru fragment */
    public LiveData<List<FriendRequest>> getRequests() {
        return data;
    }

    /** Trimite o nouă cerere — adaugă un FriendRequest în listă */
    public void sendRequest(String username) {
        // copiem lista, adăugăm și re‑publicăm
        List<FriendRequest> current = new ArrayList<>(data.getValue());
        current.add(new FriendRequest(username));
        data.setValue(current);
    }

    /** Acceptă o cerere — pur și simplu o eliminăm din listă */
    public void acceptRequest(FriendRequest req) {
        List<FriendRequest> current = new ArrayList<>(data.getValue());
        current.remove(req);
        data.setValue(current);
    }

    /** Refuză o cerere — la fel, o eliminăm */
    public void declineRequest(FriendRequest req) {
        List<FriendRequest> current = new ArrayList<>(data.getValue());
        current.remove(req);
        data.setValue(current);
    }
}
