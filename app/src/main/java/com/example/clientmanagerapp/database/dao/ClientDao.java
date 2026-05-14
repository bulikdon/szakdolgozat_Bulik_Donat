package com.example.clientmanagerapp.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.clientmanagerapp.database.entity.Client;

import java.util.List;

@Dao
public interface ClientDao {

    // Insert
    @Insert
    long insert(Client client);

    // Update
    @Update
    void update(Client client);

    // Delete
    @Delete
    void delete(Client client);

    // Get all — ABC sorrend + kedvencek felül
    @Query("SELECT * FROM clients ORDER BY favorite DESC, name ASC")
    LiveData<List<Client>> getAllClients();

    // Egy páciens ID alapján
    @Query("SELECT * FROM clients WHERE id = :clientId LIMIT 1")
    LiveData<Client> getClientById(int clientId);

    @Query("SELECT * FROM clients WHERE favorite = true LIMIT 1")
    LiveData<List<Client>> getFavoriteClients();

}
