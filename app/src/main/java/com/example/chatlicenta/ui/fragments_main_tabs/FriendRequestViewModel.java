package com.example.chatlicenta.ui.fragments_main_tabs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.chatlicenta.data.FriendRequestRepository;
import com.example.chatlicenta.data.model.FriendRequest;

import java.util.List;

/**
 * ViewModel pentru cererile de prietenie:
 *  - expune LiveData<List<FriendRequest>> pentru UI
 *  - permite trimiterea, acceptarea și refuzarea cererilor
 */
public class FriendRequestViewModel extends ViewModel {

    private final FriendRequestRepository repo = new FriendRequestRepository();

    // LiveData publică pentru lista de cereri
    private final LiveData<List<FriendRequest>> requests = repo.getRequests();

    // Intern, pentru a putea modifica lista
    private final MutableLiveData<List<FriendRequest>> mutableRequests =
            (MutableLiveData<List<FriendRequest>>) requests;

    /** Returnează lista curentă de cereri */
    public LiveData<List<FriendRequest>> getRequests() {
        return requests;
    }

    /** Trimite o nouă cerere către username */
    public void sendRequest(String username) {
        repo.sendRequest(username);
    }

    /** Acceptă cererea și o elimină din listă */
    public void acceptRequest(FriendRequest req) {
        repo.acceptRequest(req);
    }

    /** Refuză cererea și o elimină din listă */
    public void declineRequest(FriendRequest req) {
        repo.declineRequest(req);
    }
}
