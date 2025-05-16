package com.example.chatlicenta.ui.fragments_main_tabs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatlicenta.R;
import com.example.chatlicenta.data.model.Friend;
import com.example.chatlicenta.ui.chat.ChatActivity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass that displays a list of friends.
 */
public class FriendsFragment extends Fragment {
    private FriendViewModel vm;
    private RecyclerView recyclerView;
    private FriendAdapter adapter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewFriend);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FriendAdapter(friend -> {

            Toast.makeText(getContext(), "Launching chat for " + friend.getName(), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra(ChatActivity.EXTRA_CONVERSATION_ID, friend.getId());
            intent.putExtra(ChatActivity.EXTRA_CONVERSATION_TYPE, "direct");
            intent.putExtra(ChatActivity.EXTRA_CONVERSATION_TITLE, friend.getName());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        vm = new ViewModelProvider(this).get(FriendViewModel.class);
        vm.getFriends().observe(getViewLifecycleOwner(), entities -> {
            List<Friend> friends = entities.stream()
                    .map(ent -> new Friend(ent.getId(), ent.getName()))
                    .collect(Collectors.toList());
            adapter.setItems(friends);
        });
        return view;
    }
}

