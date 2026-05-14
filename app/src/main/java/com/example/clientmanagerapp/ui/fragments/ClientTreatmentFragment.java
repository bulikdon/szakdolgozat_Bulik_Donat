package com.example.clientmanagerapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clientmanagerapp.R;
import com.example.clientmanagerapp.ui.adapter.TreatmentAdapter;
import com.example.clientmanagerapp.ui.viewmodel.TreatmentViewModel;

public class ClientTreatmentFragment extends Fragment {

    private TreatmentViewModel treatmentViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.client_treatment_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int clientId = requireArguments().getInt("clientId", -1);

        Button add = view.findViewById(R.id.buttonAddTreatment);
        TextView empty = view.findViewById(R.id.textEmpty);
        RecyclerView rv = view.findViewById(R.id.recyclerTreatments);

        TreatmentAdapter adapter = new TreatmentAdapter();
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        treatmentViewModel = new ViewModelProvider(requireActivity()).get(TreatmentViewModel.class);

        treatmentViewModel.getTreatmentsForClient(clientId).observe(getViewLifecycleOwner(), list -> {
            adapter.submit(list);
            boolean isEmpty = (list == null || list.isEmpty());
            empty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            rv.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        });

        add.setOnClickListener(v -> {
            Bundle b = new Bundle();
            b.putInt("clientId", clientId);
            Navigation.findNavController(v).navigate(R.id.action_clientDetail_to_treatments, b);
        });
    }
}