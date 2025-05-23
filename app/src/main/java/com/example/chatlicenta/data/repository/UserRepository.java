package com.example.chatlicenta.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.chatlicenta.data.local.AppDatabase;
import com.example.chatlicenta.data.local.dao.UserDao;
import com.example.chatlicenta.data.local.entity.UserEntity;
import com.example.chatlicenta.data.model.LoggedInUser;
import com.example.chatlicenta.data.session.SessionManager;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.concurrent.Executors;

public class UserRepository {
    private final UserDao userDao;
    private final FirebaseAuth auth;
    private final DatabaseReference usersRef;
    private final SessionManager session;

    public UserRepository(Context ctx) {
        AppDatabase db     = AppDatabase.getInstance(ctx);
        userDao            = db.userDao();
        auth               = FirebaseAuth.getInstance();
        usersRef           = FirebaseDatabase.getInstance().getReference("users");
        session            = new SessionManager(ctx);
    }

    /** Register with email/password in FirebaseAuth */
    public Task<AuthResult> register(String email,
                                     String password,
                                     String displayName) {
        // 1) Întâi căutăm în DB un utilizator cu același displayName
        Query query = usersRef.orderByChild("displayName")
                .equalTo(displayName)
                .limitToFirst(1);

        // Vom returna un Task care se completează doar după ce terminăm tot:
        TaskCompletionSource<AuthResult> tcs = new TaskCompletionSource<>();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                if (snap.exists()) {
                    // deja există un user cu acest displayName
                    tcs.setException(new Exception("Username already taken"));
                } else {
                    // username liber → creăm contul
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener(authRes -> {
                                FirebaseUser user = authRes.getUser();
                                if (user != null) {
                                    String uid = user.getUid();
                                    // 1) scriem în Realtime DB
                                    usersRef.child(uid)
                                            .setValue(new UserEntity(uid, displayName, ""));
                                    // 2) actualizăm profilul Auth
                                    user.updateProfile(
                                            new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(displayName)
                                                    .build()
                                    );
                                    // 3) salvăm în Room
                                    Executors.newSingleThreadExecutor().execute(() ->
                                            userDao.insert(new UserEntity(uid, displayName, ""))
                                    );
                                    // 4) salvăm sesiunea
                                    session.saveUser(new LoggedInUser(uid, displayName));

                                    // semnalăm succesul mai departe
                                    tcs.setResult(authRes);
                                } else {
                                    tcs.setException(new Exception("Auth returned null user"));
                                }
                            })
                            .addOnFailureListener(tcs::setException);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                tcs.setException(error.toException());
            }
        });

        return tcs.getTask();
    }

    /** Login via FirebaseAuth */
    public Task<AuthResult> login(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(res -> {
                    FirebaseUser user = res.getUser();
                    if (user != null) {
                        String uid = user.getUid();
                        String name = user.getDisplayName();  // valoarea setată prin updateProfile
                        session.saveUser(new LoggedInUser(uid, name != null ? name : ""));
                    }
                });
    }
    public LiveData<UserEntity> getUserProfileLive() {
        String uid = session.getUserId();
        if (uid == null) return new MutableLiveData<>(null);
        return userDao.findByIdLive(uid);
    }

    /** Logout both Firebase and local session */
    public void logout() {
        auth.signOut();
        session.clearSession();
    }

    /** Get the currently logged in user from SharedPreferences */
    public LoggedInUser getCurrentUser() {
        return session.getUser();
    }


}

