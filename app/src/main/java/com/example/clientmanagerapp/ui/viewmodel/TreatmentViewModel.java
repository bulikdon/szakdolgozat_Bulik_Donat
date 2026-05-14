package com.example.clientmanagerapp.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.clientmanagerapp.database.AppDatabase;
import com.example.clientmanagerapp.database.TreatmentRepository;
import com.example.clientmanagerapp.database.entity.Treatment;

import java.util.List;

public class TreatmentViewModel extends AndroidViewModel {

    private final TreatmentRepository repository;

    public TreatmentViewModel(@NonNull Application application) {
        super(application);
        repository = new TreatmentRepository(AppDatabase.getInstance(application).treatmentDao());
    }

    public LiveData<List<Treatment>> getTreatmentsForClient(int clientId) {
        return repository.getTreatmentsForClient(clientId);
    }

    public void insert(Treatment t) {
        repository.insert(t);
    }
}