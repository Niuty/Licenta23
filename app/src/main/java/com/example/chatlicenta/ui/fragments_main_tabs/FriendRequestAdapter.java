package com.example.chatlicenta.ui.fragments_main_tabs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatlicenta.R;
import com.example.chatlicenta.data.model.FriendRequest;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {
    private List<FriendRequest> items = new ArrayList<>();

    public void setItems(List<FriendRequest> requests) {
        items = requests;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend_request, parent, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder h, int i) {
        FriendRequest req = items.get(i);
        h.username.setText(req.getUsername());
    }
    @Override public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        ViewHolder(View v) {
            super(v);
            username = v.findViewById(R.id.textViewFriendNameItem);
        }
    }
}