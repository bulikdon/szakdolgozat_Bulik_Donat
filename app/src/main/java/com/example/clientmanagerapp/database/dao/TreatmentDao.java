package com.example.clientmanagerapp.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.clientmanagerapp.database.entity.Treatment;

import java.util.List;

@Dao
public interface TreatmentDao {

    @Insert
    void insert(Treatment treatment);

    @Query("SELECT * FROM treatments WHERE clientId = :clientId ORDER BY timestamp DESC")
    LiveData<List<Treatment>> getTreatmentsForClient(int clientId);
}