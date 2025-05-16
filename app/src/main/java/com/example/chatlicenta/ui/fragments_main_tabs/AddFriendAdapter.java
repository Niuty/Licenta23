package com.example.chatlicenta.ui.fragments_main_tabs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatlicenta.R;
import com.example.chatlicenta.data.model.FriendRequest;

import java.util.ArrayList;
import java.util.List;

public class AddFriendAdapter extends RecyclerView.Adapter<AddFriendAdapter.ViewHolder> {

    public interface Callback {
        void onAccept(FriendRequest request);
        void onDecline(FriendRequest request);
    }

    private final List<FriendRequest> items = new ArrayList<>();
    private final Callback callback;

    public AddFriendAdapter(Callback callback) {
        this.callback = callback;
    }

    public void setItems(List<FriendRequest> requests) {
        items.clear();
        items.addAll(requests);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend_request, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int i) {
        FriendRequest req = items.get(i);
        h.username.setText(req.getUsername());
        // you could load an avatar into h.avatar via Glide/Picasso here
        h.accept.setOnClickListener(v -> callback.onAccept(req));
        h.decline.setOnClickListener(v -> callback.onDecline(req));
    }

    @Override public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView username;
        Button accept;
        Button decline;

        ViewHolder(View v) {
            super(v);
            avatar   = v.findViewById(R.id.imageViewRequestAvatar);
            username = v.findViewById(R.id.textViewRequestUsername);
            accept   = v.findViewById(R.id.buttonAccept);
            decline  = v.findViewById(R.id.buttonDecline);
        }
    }
}
