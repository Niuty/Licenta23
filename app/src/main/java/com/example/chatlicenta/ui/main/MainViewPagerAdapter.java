package com.example.chatlicenta.ui.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.chatlicenta.ui.fragments_main_tabs.FriendsFragment;
import com.example.chatlicenta.ui.fragments_main_tabs.GroupsFragment;
import com.example.chatlicenta.ui.fragments_main_tabs.AddFriendFragment;

public class MainViewPagerAdapter extends FragmentStateAdapter {

    public MainViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FriendsFragment();
            case 1:
                return new GroupsFragment();
            case 2:
                return new AddFriendFragment();
            default:
                return new FriendsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
