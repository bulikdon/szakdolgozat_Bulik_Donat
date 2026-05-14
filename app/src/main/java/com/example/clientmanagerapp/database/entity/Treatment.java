package com.example.clientmanagerapp.database.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "treatments",
        indices = {
                @Index("clientId")
        }
)
public class Treatment {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int clientId;           // melyik pácienshez tartozik
    public String type;            // "FULL_BODY", "BACK", stb.
    public long timestamp;         // mikor történt (ms)
    public String notes;           // panasz / megjegyzés
    public String painMapJson;     // később: JSON (pl. {"head_front":3,...})

    public Treatment(int clientId, String type, long timestamp, String notes, String painMapJson) {
        this.clientId = clientId;
        this.type = type;
        this.timestamp = timestamp;
        this.notes = notes;
        this.painMapJson = painMapJson;
    }
}