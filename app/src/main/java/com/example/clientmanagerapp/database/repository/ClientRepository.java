package com.example.clientmanagerapp.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.clientmanagerapp.database.AppDatabase;
import com.example.clientmanagerapp.database.dao.ClientDao;
import com.example.clientmanagerapp.database.entity.Client;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientRepository {

    private final ClientDao clientDao;
    private final LiveData<List<Client>> allClients;
    private final ExecutorService executorService;

    public ClientRepository(Application application) {

        AppDatabase db = AppDatabase.getInstance(application);
        clientDao = db.clientDao();

        allClients = clientDao.getAllClients();

        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Client>> getAllClients() {
        return allClients;
    }

    public LiveData<List<Client>> getFavoriteClients() {
        return clientDao.getFavoriteClients();
    }
    public LiveData<Client> getClientById(int clientId) {
        return clientDao.getClientById(clientId);
    }
    public void insert(Client client) {
        executorService.execute(() -> clientDao.insert(client));
    }

    public void update(Client client) {
        executorService.execute(() -> clientDao.update(client));
    }
    public void delete(Client client) {
        executorService.execute(() -> clientDao.delete(client));
    }
}
