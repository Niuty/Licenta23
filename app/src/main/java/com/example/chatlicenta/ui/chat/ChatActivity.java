package com.example.chatlicenta.ui.chat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatlicenta.R;
import com.example.chatlicenta.data.model.Message;

import java.util.List;

public class ChatActivity extends AppCompatActivity {

    public static final String EXTRA_CONVERSATION_ID    = "conversation_id";
    public static final String EXTRA_CONVERSATION_TYPE  = "conversation_type";
    public static final String EXTRA_CONVERSATION_TITLE = "conversation_Title";

    private ChatViewModel viewModel;
    private ChatAdapter   adapter;
    private RecyclerView  recyclerView;
    private EditText      editText;
    private ImageButton   buttonSend;

    // Trebuie să primești conversationId din Intent și userId/userName din SharedPreferences
    private String conversationId;
    private String currentUserId;
    private String currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 1) Obține parametrul conversationId
        conversationId = getIntent().getStringExtra(EXTRA_CONVERSATION_ID);

        // 2) Preia datele utilizatorului curent din SharedPreferences
        SharedPreferences prefs = getSharedPreferences("chat_prefs", MODE_PRIVATE);
        currentUserId   = prefs.getString("key_user_id", "");
        currentUserName = prefs.getString("key_user_name", "Me");

        // 3) Legături la view-uri
        recyclerView = findViewById(R.id.recyclerViewMessages);
        editText     = findViewById(R.id.editTextMessage);
        buttonSend   = findViewById(R.id.buttonSendMessageChat);

        // 4) Setup RecyclerView + Adapter
        adapter = new ChatAdapter(/* isGroupChat= */ false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // 5) Initializează ViewModel, injectând conversationId
        viewModel = new ViewModelProvider(this,
                new ViewModelProvider.Factory() {
                    @NonNull
                    @Override
                    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                        //noinspection unchecked
                        return (T) new ChatViewModel(getApplication(), conversationId);
                    }
                }).get(ChatViewModel.class);

        // 6) Observă LiveData<List<Message>> și actualizează UI direct
        viewModel.getUiMessages().observe(this, messages -> {
            adapter.setItems(messages);
            if (!messages.isEmpty()) {
                recyclerView.scrollToPosition(messages.size() - 1);
            }
        });

        // 7) Butonul Send
        buttonSend.setOnClickListener(v -> {
            String text = editText.getText().toString().trim();
            if (!text.isEmpty()) {
                viewModel.sendMessage(text);
                editText.setText("");
            }
        });
    }
}