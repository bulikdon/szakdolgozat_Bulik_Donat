package com.example.clientmanagerapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.clientmanagerapp.database.dao.ClientDao;
import com.example.clientmanagerapp.database.dao.TreatmentDao;
import com.example.clientmanagerapp.database.entity.Client;
import com.example.clientmanagerapp.database.entity.Treatment;

@Database(entities = {Client.class, Treatment.class}, version = 3) // <-- version++ (pl. 2)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract ClientDao clientDao();
    public abstract TreatmentDao treatmentDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "client_manager_db"
                            )
                            // fejlesztés alatt egyszerű:
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}