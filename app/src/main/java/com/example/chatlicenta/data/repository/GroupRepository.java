package com.example.chatlicenta.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.chatlicenta.data.local.AppDatabase;
import com.example.chatlicenta.data.local.dao.GroupDao;
import com.example.chatlicenta.data.local.entity.GroupEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;

public class GroupRepository {
    private final GroupDao dao;
    private final ExecutorService exec = Executors.newSingleThreadExecutor();

    public GroupRepository(Context ctx) {
        dao = AppDatabase.getInstance(ctx).groupDao();
    }

    public LiveData<List<GroupEntity>> getAllGroups() {
        return dao.getAllGroupsLive();
    }

    public void addGroup(GroupEntity g) {
        exec.execute(() -> dao.insert(g));
    }

    public void removeGroup(String id) {
        exec.execute(() -> {
            GroupEntity ent = dao.findById(id);
            if (ent != null) dao.delete(ent);
        });
    }
}
