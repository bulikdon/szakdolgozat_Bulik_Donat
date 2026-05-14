package com.example.clientmanagerapp.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;
import com.example.clientmanagerapp.database.entity.PainZone;

@Dao
public interface PainZoneDao {

    @Insert
    void insertAll(List<PainZone> zones);

    @Query("SELECT * FROM pain_zones WHERE treatmentId = :treatmentId")
    List<PainZone> getByTreatment(int treatmentId);
}
