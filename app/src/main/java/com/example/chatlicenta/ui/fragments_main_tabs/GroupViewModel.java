// app/src/main/java/com/example/chatlicenta/ui/fragments_main_tabs/GroupViewModel.java
package com.example.chatlicenta.ui.fragments_main_tabs;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.chatlicenta.data.repository.GroupRepository;
import com.example.chatlicenta.data.local.entity.GroupEntity;

import java.util.List;

public class GroupViewModel extends AndroidViewModel {
    private final GroupRepository repo;
    private final LiveData<List<GroupEntity>> groups;

    public GroupViewModel(@NonNull Application app) {
        super(app);
        repo   = new GroupRepository(app);
        groups = repo.getAllGroups();
    }

    public LiveData<List<GroupEntity>> getGroups() {
        return groups;
    }

    public void addGroup(GroupEntity g) {
        repo.addGroup(g);
    }

    public void removeGroup(String id) {
        repo.removeGroup(id);
    }
}
