package com.example.clientmanagerapp.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "pain_zones",
        foreignKeys = @ForeignKey(
                entity = Treatment.class,
                parentColumns = "id",
                childColumns = "treatmentId",
                onDelete = CASCADE
        )
)
public class PainZone {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int treatmentId;
    public String zoneKey;
    public int painLevel;

    public PainZone(int treatmentId, String zoneKey, int painLevel) {
        this.treatmentId = treatmentId;
        this.zoneKey = zoneKey;
        this.painLevel = painLevel;
    }
}
