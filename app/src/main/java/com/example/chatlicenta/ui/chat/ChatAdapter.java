package com.example.chatlicenta.ui.chat;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatlicenta.R;
import com.example.chatlicenta.data.model.Message;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private final boolean isGroupChat;
    // keep a non-null list so we never have to guard later
    private final List<Message> items = new ArrayList<>();

    // cache a locale-aware time formatter
    private final DateFormat timeFormat =
            DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());

    public ChatAdapter(boolean isGroupChat) {
        this.isGroupChat = isGroupChat;
    }

    /**
     * Supply a new list of messages (sent and received).
     */
    public void setItems(List<Message> msgs) {
        items.clear();
        if (msgs != null) {
            items.addAll(msgs);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        Message m = items.get(pos);

        if (isGroupChat) {
            h.senderName.setVisibility(View.VISIBLE);
            h.senderName.setText(m.getSenderName());

            // 1) Aliniază numele expeditorului
            LinearLayout.LayoutParams nameLp =
                    (LinearLayout.LayoutParams) h.senderName.getLayoutParams();
            nameLp.gravity = m.isSent() ? Gravity.END : Gravity.START;
            h.senderName.setLayoutParams(nameLp);

            // 2) Aliniază și bula de mesaj
            LinearLayout.LayoutParams bubbleLp =
                    (LinearLayout.LayoutParams) h.bubble.getLayoutParams();
            bubbleLp.gravity = m.isSent() ? Gravity.END : Gravity.START;
            h.bubble.setLayoutParams(bubbleLp);
        } else {
            h.senderName.setVisibility(View.GONE);
        }

        // 1) Set the message text
        h.text.setText(m.getText());

        // 2) Format and set the time
        h.time.setText(timeFormat.format(new Date(m.getTimestamp())));

        // 3) Bubble background & alignment
        int bgRes = m.isSent()
                ? R.drawable.bg_message_sent
                : R.drawable.bg_message_received;
        h.bubble.setBackgroundResource(bgRes);
        LinearLayout.LayoutParams lp =
                (LinearLayout.LayoutParams) h.bubble.getLayoutParams();
        lp.gravity = m.isSent() ? Gravity.END : Gravity.START;
        h.bubble.setLayoutParams(lp);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * ViewHolder holds references to our bubble container, text, and time views
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        final LinearLayout bubble;
        final TextView text;
        final TextView time;
        final TextView senderName;

        ViewHolder(View v) {
            super(v);
            senderName = v.findViewById(R.id.textViewSenderName);
            bubble = v.findViewById(R.id.containerBubble);
            text = v.findViewById(R.id.textViewMessage);
            time = v.findViewById(R.id.textViewTime);
        }
    }
}
