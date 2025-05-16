package com.example.chatlicenta.ui.fragments_main_tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatlicenta.R;
import com.example.chatlicenta.data.model.FriendRequest;

/**
 * A simple {@link Fragment} subclass that provides UI to add a new friend.
 */
public class AddFriendFragment extends Fragment {

    private EditText editTextFriendUsername;
    private Button buttonAddFriend;
    private FriendRequestViewModel vm;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private AddFriendAdapter adapter;  // use AddFriendAdapter here

    public AddFriendFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_friend, container, false);

        editTextFriendUsername = view.findViewById(R.id.editTextFriendUsername);
        buttonAddFriend = view.findViewById(R.id.buttonAddFriend);
        emptyView = view.findViewById(R.id.textViewNoRequests);
        recyclerView = view.findViewById(R.id.recyclerViewRequests);

        // Initialize adapter with callbacks
        adapter = new AddFriendAdapter(new AddFriendAdapter.Callback() {
            @Override public void onAccept(FriendRequest request) { vm.acceptRequest(request); }
            @Override public void onDecline(FriendRequest request) { vm.declineRequest(request); }
        });
        // 2) Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // 3) ViewModel & observe
        vm = new ViewModelProvider(this).get(FriendRequestViewModel.class);
        vm.getRequests().observe(getViewLifecycleOwner(), list -> {
            if (list.isEmpty()) {
                emptyView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.setItems(list);
            }
        });

        // 4) Button logic
        buttonAddFriend.setOnClickListener(v -> {
            String username = editTextFriendUsername.getText().toString().trim();
            if (username.isEmpty()) {
                Toast.makeText(getContext(), "Introduceți un nume!", Toast.LENGTH_SHORT).show();
            } else {
                vm.sendRequest(username);
                editTextFriendUsername.setText("");
            }
        });

        vm.sendRequest("Alice");
        vm.sendRequest("Bob");
        vm.sendRequest("Carol");
        vm.sendRequest("Alice");



        return view;
    }
}
