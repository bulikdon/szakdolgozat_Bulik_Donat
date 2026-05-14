package com.example.clientmanagerapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.clientmanagerapp.R;
import com.example.clientmanagerapp.database.AppDatabase;
import com.example.clientmanagerapp.database.dao.TreatmentDao;
import com.example.clientmanagerapp.database.entity.Treatment;
import com.example.clientmanagerapp.ui.viewmodel.PainMapAbsView;

public class AbsTreatmentFragment extends Fragment {

    private PainMapAbsView absView;
    private EditText editTextNotes;
    private Button buttonSave;

    private int clientId;
    private TreatmentDao treatmentDao;

    public AbsTreatmentFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.abs_treatment_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // args-ból jön
        if (getArguments() != null) {
            clientId = getArguments().getInt("CLIENT_ID", -1);
        } else {
            clientId = -1;
        }

        if (clientId == -1) {
            Toast.makeText(requireContext(), "Missing CLIENT_ID", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).popBackStack();
            return;
        }

        treatmentDao = AppDatabase.getInstance(requireContext().getApplicationContext()).treatmentDao();

        absView = view.findViewById(R.id.absPainView);
        editTextNotes = view.findViewById(R.id.editTextNotesAbs);
        buttonSave = view.findViewById(R.id.buttonSaveAbs);

        buttonSave.setOnClickListener(v -> saveTreatment());
    }

    private void saveTreatment() {
        String notes = editTextNotes.getText().toString().trim();
        String painJson = absView.exportPainMapJson();
        long ts = System.currentTimeMillis();

        Treatment t = new Treatment(
                clientId,
                "ABS",
                ts,
                notes,
                painJson
        );

        new Thread(() -> {
            treatmentDao.insert(t);
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).popBackStack();
                NavHostFragment.findNavController(this).popBackStack();

            });
        }).start();
    }
}