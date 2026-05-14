package com.example.clientmanagerapp.database;

import androidx.lifecycle.LiveData;

import com.example.clientmanagerapp.database.dao.TreatmentDao;
import com.example.clientmanagerapp.database.entity.Treatment;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TreatmentRepository {

    private final TreatmentDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public TreatmentRepository(TreatmentDao dao) {
        this.dao = dao;
    }

    public LiveData<List<Treatment>> getTreatmentsForClient(int clientId) {
        return dao.getTreatmentsForClient(clientId);
    }

    public void insert(Treatment t) {
        executor.execute(() -> dao.insert(t));
    }
}