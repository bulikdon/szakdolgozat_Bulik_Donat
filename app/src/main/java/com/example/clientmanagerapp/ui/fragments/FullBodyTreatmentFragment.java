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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.clientmanagerapp.R;
import com.example.clientmanagerapp.database.entity.Treatment;
import com.example.clientmanagerapp.ui.viewmodel.TreatmentViewModel;
import com.example.clientmanagerapp.ui.viewmodel.PainMapViewModel;

import org.json.JSONObject;

import java.util.Map;

public class FullBodyTreatmentFragment extends Fragment {

    private TreatmentViewModel treatmentViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.full_body_treatment_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int clientId = requireArguments().getInt("clientId", -1);

        treatmentViewModel = new ViewModelProvider(requireActivity()).get(TreatmentViewModel.class);

        PainMapViewModel painMapView = view.findViewById(R.id.painMapView);
        EditText editNotes = view.findViewById(R.id.editNotes);
        Button save = view.findViewById(R.id.buttonSaveTreatment);

        save.setOnClickListener(v -> {
            String notes = editNotes.getText().toString().trim();

            // painMap -> JSON
            String painMapJson = toJson(painMapView.getPainMap());

            Treatment t = new Treatment(
                    clientId,
                    "FULL_BODY",
                    System.currentTimeMillis(),
                    notes,
                    painMapJson
            );

            treatmentViewModel.insert(t);

            Toast.makeText(requireContext(), "Treatment saved", Toast.LENGTH_SHORT).show();

            Navigation.findNavController(v).popBackStack();
            Navigation.findNavController(v).popBackStack();
        });
    }

    private String toJson(Map<String, Integer> map) {
        try {
            JSONObject obj = new JSONObject();
            for (Map.Entry<String, Integer> e : map.entrySet()) {
                obj.put(e.getKey(), e.getValue());
            }
            return obj.toString();
        } catch (Exception ex) {
            return "{}";
        }
    }
}