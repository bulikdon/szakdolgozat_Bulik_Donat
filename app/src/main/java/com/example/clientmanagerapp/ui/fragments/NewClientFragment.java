package com.example.clientmanagerapp.ui.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.clientmanagerapp.R;
import com.example.clientmanagerapp.database.entity.Client;
import com.example.clientmanagerapp.models.Sex;
import com.example.clientmanagerapp.ui.viewmodel.ClientViewModel;

import java.util.Calendar;
import java.util.Locale;

public class NewClientFragment extends Fragment {

    private ClientViewModel viewModel;

    private EditText inputName, inputBirthDate, inputPhone, inputHeight, inputWeight;
    private Spinner inputSex;
    private SeekBar inputDistribution;
    private TextView textDistributionValue;
    private Button buttonSave;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.new_client_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ClientViewModel.class);

        inputName = view.findViewById(R.id.inputName);
        inputSex = view.findViewById(R.id.inputSex);
        inputBirthDate = view.findViewById(R.id.inputBirthDate);
        inputBirthDate.setOnClickListener(v -> {

            Calendar cal = Calendar.getInstance();

            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog picker = new DatePickerDialog(
                    requireContext(),
                    (view1, y, m, d) -> {

                        String formatted = String.format(
                                Locale.US,
                                "%04d-%02d-%02d",
                                y,
                                (m + 1),
                                d
                        );

                        inputBirthDate.setText(formatted);

                    },
                    year,
                    month,
                    day
            );

            picker.show();
        });
        inputPhone = view.findViewById(R.id.inputPhone);
        inputHeight = view.findViewById(R.id.inputHeight);
        inputWeight = view.findViewById(R.id.inputWeight);
        inputDistribution = view.findViewById(R.id.inputDistribution);
        textDistributionValue = view.findViewById(R.id.textDistributionValue);
        buttonSave = view.findViewById(R.id.buttonSave);

        // Spinner: Sex enum
        ArrayAdapter<Sex> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                Sex.values()
        );
        inputSex.setAdapter(adapter);

        // SeekBar: % kiírás
        updateDistributionText(inputDistribution.getProgress());
        inputDistribution.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateDistributionText(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Save
        buttonSave.setOnClickListener(v -> {
            String name = inputName.getText().toString().trim();
            String birth = inputBirthDate.getText().toString().trim();
            String phone = inputPhone.getText().toString().trim();

            String heightStr = inputHeight.getText().toString().trim();
            String weightStr = inputWeight.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(requireContext(), "Name is required", Toast.LENGTH_SHORT).show();
                return;
            }

            float height = 0f;
            float weight = 0f;
            try {
                if (!TextUtils.isEmpty(heightStr)) height = Float.parseFloat(heightStr);
                if (!TextUtils.isEmpty(weightStr)) weight = Float.parseFloat(weightStr);
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Height/Weight must be a number", Toast.LENGTH_SHORT).show();
                return;
            }

            Sex sex = (Sex) inputSex.getSelectedItem();
            int distribution = inputDistribution.getProgress();

            Client client = new Client(
                    name,
                    sex,
                    birth,
                    phone,
                    height,
                    weight,
                    distribution,
                    false, // favorite default
                    System.currentTimeMillis()
            );

            viewModel.insert(client);

            Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show();

            // ✅ vissza Existing fülre
            NavHostFragment.findNavController(this).navigate(R.id.existingClientsFragment);
        });
    }

    private void updateDistributionText(int rightPercent) {
        int left = 100 - rightPercent;
        textDistributionValue.setText("Left " + left + "% — Right " + rightPercent + "%");
    }
}
