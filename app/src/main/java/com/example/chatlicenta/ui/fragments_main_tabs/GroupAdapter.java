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
import com.example.chatlicenta.data.local.entity.GroupEntity;

import java.util.ArrayList;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.VH> {
    public interface Callback {
        void onGroupClick(GroupEntity g);
    }

    private final List<GroupEntity> items = new ArrayList<>();
    private final Callback callback;

    public GroupAdapter(Callback cb) {
        this.callback = cb;
    }

    public void setItems(List<GroupEntity> list) {
        items.clear();
        items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int i) {
        View v = LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_group, p, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int i) {
        GroupEntity g = items.get(i);
        if (g.getPhotoUri() != null && !g.getPhotoUri().isEmpty()) {
            Glide.with(h.itemView.getContext())
                    .load(g.getPhotoUri())
                    .placeholder(R.drawable.ic_hooded_placeholder)
                    .circleCrop()
                    .into(h.avatar);
        } else {
            h.avatar.setImageResource(R.drawable.ic_hooded_placeholder);
        }
        h.name.setText(g.getName());
        h.count.setText(g.getMemberCount() + " membri");
        h.itemView.setOnClickListener(v -> callback.onGroupClick(g));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView name, count;
        ImageView avatar;

        VH(View v) {
            super(v);
            avatar = v.findViewById(R.id.friendGroupPictureItem);
            name = v.findViewById(R.id.textViewGroupNameItem);
            count = v.findViewById(R.id.textViewGroupNumberMembersItem);
        }
    }
}
