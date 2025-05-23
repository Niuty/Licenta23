package com.example.chatlicenta.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatlicenta.data.local.AppDatabase;
import com.example.chatlicenta.data.local.dao.FriendDao;
import com.example.chatlicenta.data.local.dao.GroupDao;
import com.example.chatlicenta.data.local.entity.FriendEntity;
import com.example.chatlicenta.data.local.entity.GroupEntity;
import com.example.chatlicenta.data.repository.FriendRepository;
import com.example.chatlicenta.databinding.ActivityMainBinding;
import com.example.chatlicenta.ui.main.MainViewPagerAdapter;
import com.example.chatlicenta.ui.settings.SettingsActivity;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    // TO DOO : Logout în MainActivity (locația UPDATE)
    //
    //Unde ai butonul de logout, adaugă:
    //
    //// onLogoutClicked()
    //SessionManager session = new SessionManager(this);
    //session.clearSession();
    //startActivity(new Intent(this, LoginActivity.class));
    //finish();
    private ActivityMainBinding binding;
    private final String[] tabTitles = new String[]{"Friends", "Groups", "Add Friend"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up the ViewPager2 with the MainViewPagerAdapter
        MainViewPagerAdapter adapter = new MainViewPagerAdapter(this);
        binding.viewPager.setAdapter(adapter);

        // Link the TabLayout and the ViewPager2
        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> tab.setText(tabTitles[position])
        ).attach();



//        new Thread(() -> {
//            FriendDao dao = AppDatabase.getInstance(this).friendDao();
//            // Dacă nu există deja, inserează câteva prieteni de test
//            if (dao.getAllFriendsSync().isEmpty()) {
//                dao.insert(new FriendEntity("1", "Alice", null));
//                dao.insert(new FriendEntity("2", "Bob",   null));
//                dao.insert(new FriendEntity("3", "Carol", null));
//                dao.insert(new FriendEntity("4", "Andrada", null));
//            }
//        }).start();

        // 2) Pornește sincronizarea listei de prieteni
        FriendRepository friendRepo = new FriendRepository(this);
        friendRepo.subscribeAndCache();

        new Thread(() -> {
            GroupDao dao = AppDatabase.getInstance(this).groupDao();
            if (dao.getAllGroupsLive().getValue() == null || dao.getAllGroupsLive().getValue().isEmpty()) {
                dao.insert(new GroupEntity("g1","Study Group", 5, "https://example.com/avatar1.png"));
                dao.insert(new GroupEntity("g2","Family",      4, null));  // will use placeholder
            }
        }).start();


        binding.settingsButton.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });
    }
}
