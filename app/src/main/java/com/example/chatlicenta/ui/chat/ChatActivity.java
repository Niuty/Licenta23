package com.example.chatlicenta.ui.chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chatlicenta.data.FriendRepository;
import com.example.chatlicenta.data.local.entity.FriendEntity;
import com.example.chatlicenta.data.local.entity.MessageEntity;
import com.example.chatlicenta.data.session.SessionManager;
import com.example.chatlicenta.databinding.ActivityChatBinding;
import com.example.chatlicenta.data.model.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {
    public static final String EXTRA_CONVERSATION_ID    = "conversation_id";
    public static final String EXTRA_CONVERSATION_TYPE  = "conversation_type";
    public static final String EXTRA_CONVERSATION_TITLE = "conversation_Title";

    private ActivityChatBinding binding;
    private ChatViewModel       viewModel;
    private ChatAdapter         adapter;
    private String              conversationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1) Intent parameters
        conversationId   = getIntent().getStringExtra(EXTRA_CONVERSATION_ID);
        String conversationType = getIntent().getStringExtra(EXTRA_CONVERSATION_TYPE);
        String title     = getIntent().getStringExtra(EXTRA_CONVERSATION_TITLE);
        boolean isGroup  = "group".equals(conversationType);

        if (title != null) {
            binding.textViewChatNameItem.setText(title);
        }

        // 2) RecyclerView + Adapter
        adapter = new ChatAdapter(isGroup);
        binding.recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewMessages.setAdapter(adapter);

        // 3) ViewModel
        viewModel = new ViewModelProvider(
                this,
                new ChatViewModelFactory(getApplication(), conversationId)
        ).get(ChatViewModel.class);

        // 4) Read currentUserId from prefs only (no Room)
        SessionManager session      = new SessionManager(this);
        String currentUserId        = session.getUserId();

        // 5) Build name-lookup off the main thread
        ExecutorService exec = Executors.newSingleThreadExecutor();
        exec.execute(() -> {
            FriendRepository friendRepo = new FriendRepository(this);
            List<FriendEntity> friends  = friendRepo.getAllFriendsSync();

            Map<String,String> nameLookup = new HashMap<>();
            for (FriendEntity fe : friends) {
                nameLookup.put(fe.getId(), fe.getName());
            }

            // 6) Back to UI thread: observe messages
            runOnUiThread(() ->
                    setupMessagesObserver(nameLookup, currentUserId)
            );
        });

        // 7) Send message actions
        binding.buttonSend.setOnClickListener(v -> sendMessage());
        binding.editTextMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                return true;
            }
            return false;
        });
    }

    /** Observe LiveData and map each MessageEntity → Message using our lookup and userId */
    private void setupMessagesObserver(
            Map<String,String> nameLookup,
            String currentUserId
    ) {
        viewModel.getMessages().observe(this, entities -> {
            List<Message> messages = new ArrayList<>(entities.size());
            for (MessageEntity e : entities) {
                boolean sent = e.getSenderId().equals(currentUserId);
                String  name = sent
                        ? "Me"
                        : nameLookup.getOrDefault(e.getSenderId(), "Unknown");

                messages.add(new Message(
                        e.getConversationId(),
                        e.getSenderId(),
                        name,
                        e.getContent(),
                        e.getTimestamp(),
                        sent
                ));
            }
            adapter.setItems(messages);
            if (!messages.isEmpty()) {
                binding.recyclerViewMessages
                        .scrollToPosition(messages.size() - 1);
            }
        });
    }

    private void sendMessage() {
        String text = binding.editTextMessage.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, "Nu poți trimite mesaj gol",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.sendMessage(text);
        binding.editTextMessage.setText("");
    }
}
