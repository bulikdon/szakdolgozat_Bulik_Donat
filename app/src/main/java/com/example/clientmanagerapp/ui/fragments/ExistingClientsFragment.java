package com.example.clientmanagerapp.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clientmanagerapp.R;
import com.example.clientmanagerapp.database.entity.Client;
import com.example.clientmanagerapp.models.Sex;
import com.example.clientmanagerapp.ui.adapter.ClientAdapter;
import com.example.clientmanagerapp.ui.viewmodel.ClientViewModel;

import java.util.ArrayList;
import java.util.List;

public class ExistingClientsFragment extends Fragment {

    private ClientViewModel viewModel;
    private ClientAdapter adapter;

    private List<Client> allClients = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.existing_clients_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ClientViewModel.class);

        RecyclerView recycler = view.findViewById(R.id.recyclerClients);
        EditText search = view.findViewById(R.id.inputSearch);

        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ClientAdapter(new ArrayList<>(), viewModel);
        recycler.setAdapter(adapter);

        // LiveData figyelés
        viewModel.getAllClients().observe(getViewLifecycleOwner(), clients -> {
            allClients = clients;

            // ✅ teszt client beszúrás, ha üres a DB (egyszer)
            if (clients == null || clients.isEmpty()) {
                Client test = new Client(
                        "Test Patient",
                        Sex.MALE,
                        "1990-01-01",
                        "+361234567",
                        180f,
                        80f,
                        50,
                        true,
                        System.currentTimeMillis()
                );
                viewModel.insert(test);
                return;
            }

            adapter.update(clients);
        });

        // Keresés névre
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String q = s.toString().trim().toLowerCase();

                List<Client> filtered = new ArrayList<>();
                for (Client c : allClients) {
                    if (c.getName() != null && c.getName().toLowerCase().contains(q)) {
                        filtered.add(c);
                    }
                }
                adapter.update(filtered);
            }
        });
    }
}
