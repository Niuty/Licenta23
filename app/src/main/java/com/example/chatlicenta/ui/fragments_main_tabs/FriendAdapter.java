package com.example.chatlicenta.ui.fragments_main_tabs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatlicenta.R;
import com.example.chatlicenta.data.model.Friend;

import java.util.ArrayList;
import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    public interface Callback {
        void onFriendClick(Friend friend);
    }

    private List<Friend> items = new ArrayList<>();
    private final Callback callback;

    public FriendAdapter(Callback callback) {
        this.callback = callback;
    }

    public void setItems(List<Friend> friends) {
        items = friends;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int i) {
        Friend friend = items.get(i);
        h.name.setText(friend.getName());
        if (friend.getPhotoUri() != null && !friend.getPhotoUri().isEmpty()) {
            Glide.with(h.itemView.getContext())
                    .load(friend.getPhotoUri())
                    .placeholder(R.drawable.ic_hooded_placeholder)
                    .circleCrop()
                    .into(h.avatar);
        } else {
            h.avatar.setImageResource(R.drawable.ic_hooded_placeholder);
        }
        // aici legăm click‑ul
        h.itemView.setOnClickListener(v -> callback.onFriendClick(friend));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView name;
        ViewHolder(View v) {
            super(v);
            avatar = v.findViewById(R.id.friendProfilePictureItem);
            name   = v.findViewById(R.id.textViewFriendNameItem);

        }
    }
}
