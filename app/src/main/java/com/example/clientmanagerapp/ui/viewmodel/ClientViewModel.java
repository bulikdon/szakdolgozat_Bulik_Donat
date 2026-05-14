package com.example.clientmanagerapp.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.clientmanagerapp.database.entity.Client;
import com.example.clientmanagerapp.database.repository.ClientRepository;

import java.util.List;

public class ClientViewModel extends AndroidViewModel {

    private final ClientRepository repository;
    private final LiveData<List<Client>> allClients;
    private final LiveData<List<Client>> favoriteClients;

    public ClientViewModel(@NonNull Application application) {
        super(application);

        repository = new ClientRepository(application);
        allClients = repository.getAllClients();
        favoriteClients = repository.getFavoriteClients();
    }

    // -----------------------------
    // GETTERS (UI observe-olja)
    // -----------------------------

    public LiveData<List<Client>> getAllClients() {
        return allClients;
    }

    public LiveData<List<Client>> getFavoriteClients() {
        return favoriteClients;
    }

    public LiveData<Client> getClientById(int clientId) {
        return repository.getClientById(clientId);
    }

    // -----------------------------
    // ACTIONS (UI hívja)
    // -----------------------------

    public void insert(Client client) {
        repository.insert(client);
    }

    public void update(Client client) {
        repository.update(client);
    }

    public void delete(Client client) {
        repository.delete(client);
    }
}
