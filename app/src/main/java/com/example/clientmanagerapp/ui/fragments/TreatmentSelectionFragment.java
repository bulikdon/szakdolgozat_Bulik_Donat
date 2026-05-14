package com.example.clientmanagerapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.clientmanagerapp.R;

public class TreatmentSelectionFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.treatment_selection_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int clientId = requireArguments().getInt("clientId", -1);

        Button b1 = view.findViewById(R.id.buttonTreatment1);
        Button b2 = view.findViewById(R.id.buttonTreatment2);
        Button b3 = view.findViewById(R.id.buttonTreatment3);

        // később itt navigálunk a konkrét treatment képernyőkre (full body pain map, stb.)
        b1.setOnClickListener(v -> {
            Bundle b = new Bundle();
            b.putInt("clientId", clientId);
            Navigation.findNavController(v).navigate(R.id.action_treatments_to_fullBody, b);
        });

        b2.setOnClickListener(v -> {
            Bundle b = new Bundle();
            b.putInt("CLIENT_ID", clientId);
            NavHostFragment.findNavController(this).navigate(R.id.absTreatmentFragment, b);
        });

        b3.setOnClickListener(v -> {
            Bundle b = new Bundle();
            b.putInt("CLIENT_ID", clientId);
            NavHostFragment.findNavController(this).navigate(R.id.footTreatmentFragment, b);
        });
    }
}
