package com.example.chatlicenta.ui.fragments_main_tabs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatlicenta.R;
import com.example.chatlicenta.ui.chat.ChatActivity;

public class GroupsFragment extends Fragment {

    private GroupViewModel vm;
    private RecyclerView recyclerView;
    private GroupAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewGroups);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GroupAdapter(group -> {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra(ChatActivity.EXTRA_CONVERSATION_ID, group.getId());
            intent.putExtra(ChatActivity.EXTRA_CONVERSATION_TYPE, "group");
            intent.putExtra(ChatActivity.EXTRA_CONVERSATION_TITLE, group.getName());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        vm = new ViewModelProvider(this).get(GroupViewModel.class);
        vm.getGroups().observe(getViewLifecycleOwner(), list -> {
            adapter.setItems(list);
        });

        return view;
    }
}
