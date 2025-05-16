package com.example.chatlicenta.ui.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.chatlicenta.R;
import com.example.chatlicenta.data.model.LoggedInUser;
import com.example.chatlicenta.databinding.FragmentSettingsBinding;
import com.example.chatlicenta.ui.login.LoginActivity;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SettingsViewModel viewModel;
    private ActivityResultLauncher<String> pickImageLauncher;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1) ViewModel
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        // 2) Observăm LiveData<LoggedInUser> și actualizăm UI
        viewModel.getUser().observe(getViewLifecycleOwner(), this::bindUser);

        // 3) Registrăm ImagePicker
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        // afișăm imediat în ImageView
                        Glide.with(this)
                                .load(uri)
                                .circleCrop()
                                .into(binding.imageViewAvatar);
                        // persistăm noua poză
                        viewModel.changePhoto(uri);
                    }
                }
        );

        // 4) Buton schimbare foto
        binding.buttonChangePhoto.setOnClickListener(v ->
                pickImageLauncher.launch("image/*")
        );

        // 5) Buton salvare nume
        binding.buttonSaveName.setOnClickListener(v -> {
            String newName = binding.editTextDisplayName.getText().toString().trim();
            if (newName.isEmpty()) {
                binding.editTextDisplayName.setError(
                        getString(R.string.error_name_empty));
            } else {
                viewModel.changeName(newName);
                Toast.makeText(requireContext(),
                        getString(R.string.name_updated), Toast.LENGTH_SHORT).show();
            }
        });

        // 6) Buton logout
        binding.buttonLogout.setOnClickListener(v -> {
            viewModel.logout();
            startActivity(new Intent(requireContext(), LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        });
    }

    /** Leagă datele din LoggedInUser în widget‑uri */
    private void bindUser(LoggedInUser user) {
        if (user == null) return;
        binding.editTextDisplayName.setText(user.getDisplayName());
        String uri = user.getPhotoUri();
        if (uri != null && !uri.isEmpty()) {
            Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.ic_hooded_placeholder)
                    .circleCrop()
                    .into(binding.imageViewAvatar);
        } else {
            // placeholder default
            binding.imageViewAvatar.setImageResource(
                    R.drawable.ic_hooded_placeholder);
        }
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
