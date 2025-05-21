package com.example.chatlicenta.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.chatlicenta.data.local.dao.GroupDao;
import com.example.chatlicenta.data.local.dao.MessageDao;
import com.example.chatlicenta.data.local.dao.UserDao;
import com.example.chatlicenta.data.local.dao.FriendDao;               // ← import nou
import com.example.chatlicenta.data.local.dao.UserFriendCrossRefDao;
import com.example.chatlicenta.data.local.entity.GroupEntity;
import com.example.chatlicenta.data.local.entity.MessageEntity;
import com.example.chatlicenta.data.local.entity.UserEntity;
import com.example.chatlicenta.data.local.entity.FriendEntity;         // ← import nou
import com.example.chatlicenta.data.local.entity.UserFriendCrossRef;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {
                UserEntity.class,
                FriendEntity.class,
                GroupEntity.class,
                MessageEntity.class,
                UserFriendCrossRef.class
        },
        version = 7,                                  // ← bump de versiune
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(4);

    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();
    public abstract FriendDao friendDao();
    public abstract GroupDao groupDao();
    public abstract MessageDao messageDao();
    public abstract UserFriendCrossRefDao userFriendCrossRefDao();

    public static AppDatabase getInstance(Context ctx) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    ctx.getApplicationContext(),
                                    AppDatabase.class,
                                    "chat_licenta_db"
                            )
                            // Dacă nu vrei să scrii manual o migrație:
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
