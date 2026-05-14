package com.example.clientmanagerapp.ui.fragments;

import android.app.AlertDialog;
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

public class ClientInfoFragment extends Fragment {

    private ClientViewModel viewModel;
    private Client currentClient;

    private EditText editName, editBirthDate, editPhone, editHeight, editWeight;
    private Spinner spinnerSex;
    private SeekBar seekDistribution;
    private TextView textDistribution;

    private Button buttonEdit, buttonSave, buttonDelete;

    private boolean editMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.client_info_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int clientId = requireArguments().getInt("clientId", -1);
        if (clientId == -1) {
            Toast.makeText(requireContext(), "Missing clientId", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).popBackStack();
            return;
        }

        viewModel = new ViewModelProvider(requireActivity()).get(ClientViewModel.class);

        // Views
        editName = view.findViewById(R.id.editName);
        spinnerSex = view.findViewById(R.id.spinnerSex);
        editBirthDate = view.findViewById(R.id.editBirthDate);
        editPhone = view.findViewById(R.id.editPhone);
        editHeight = view.findViewById(R.id.editHeight);
        editWeight = view.findViewById(R.id.editWeight);
        seekDistribution = view.findViewById(R.id.seekDistribution);
        textDistribution = view.findViewById(R.id.textDistribution);

        buttonEdit = view.findViewById(R.id.buttonEdit);
        buttonSave = view.findViewById(R.id.buttonSave);
        buttonDelete = view.findViewById(R.id.buttonDelete);

        // Spinner setup
        ArrayAdapter<Sex> sexAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                Sex.values()
        );
        sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSex.setAdapter(sexAdapter);

        // Distribution preview
        seekDistribution.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateDistributionLabel(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Birthdate datepicker (csak edit módban engedjük)
        editBirthDate.setOnClickListener(v -> {
            if (!editMode) return;
            showDatePicker(editBirthDate.getText().toString().trim());
        });

        // Load client
        viewModel.getClientById(clientId).observe(getViewLifecycleOwner(), client -> {
            if (client == null) return;

            currentClient = client;

            // Fill fields
            editName.setText(safe(client.name));
            editBirthDate.setText(safe(client.birthDate));
            editPhone.setText(safe(client.phone));
            editHeight.setText(String.valueOf(client.height));
            editWeight.setText(String.valueOf(client.weight));

            Sex sex = client.sex;
            spinnerSex.setSelection(sex != null ? sex.ordinal() : 0);

            int right = clamp0to100(client.distribution);
            seekDistribution.setProgress(right);
            updateDistributionLabel(right);

            setEditMode(false);
        });

        buttonEdit.setOnClickListener(v -> setEditMode(true));
        buttonSave.setOnClickListener(v -> save());
        buttonDelete.setOnClickListener(v -> confirmDelete());
    }

    private void setEditMode(boolean enabled) {
        editMode = enabled;

        editName.setEnabled(enabled);
        spinnerSex.setEnabled(enabled);
        editBirthDate.setEnabled(enabled);
        editPhone.setEnabled(enabled);
        editHeight.setEnabled(enabled);
        editWeight.setEnabled(enabled);
        seekDistribution.setEnabled(enabled);

        buttonSave.setEnabled(enabled);
        buttonEdit.setEnabled(!enabled);
    }

    private void updateDistributionLabel(int right) {
        int left = 100 - right;
        textDistribution.setText("Left " + left + "% — Right " + right + "%");
    }

    private void showDatePicker(String current) {
        Calendar cal = Calendar.getInstance();

        try {
            if (!TextUtils.isEmpty(current) && current.length() >= 10) {
                int y = Integer.parseInt(current.substring(0, 4));
                int m = Integer.parseInt(current.substring(5, 7)) - 1;
                int d = Integer.parseInt(current.substring(8, 10));
                cal.set(y, m, d);
            }
        } catch (Exception ignored) {}

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dlg = new DatePickerDialog(requireContext(),
                (dp, y, m, d) -> {
                    String formatted = String.format(Locale.US, "%04d-%02d-%02d", y, (m + 1), d);
                    editBirthDate.setText(formatted);
                },
                year, month, day
        );
        dlg.show();
    }

    private void save() {
        if (currentClient == null) return;

        String name = editName.getText().toString().trim();
        String birthDate = editBirthDate.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        Sex sex = (Sex) spinnerSex.getSelectedItem();

        String heightStr = editHeight.getText().toString().trim();
        String weightStr = editWeight.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Name is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (birthDate.length() != 10) {
            Toast.makeText(requireContext(), "Birth date must be yyyy-MM-dd", Toast.LENGTH_SHORT).show();
            return;
        }

        float height;
        float weight;
        try {
            height = Float.parseFloat(heightStr);
            weight = Float.parseFloat(weightStr);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Height/Weight must be a number", Toast.LENGTH_SHORT).show();
            return;
        }

        int distributionRight = clamp0to100(seekDistribution.getProgress());

        // Update fields
        currentClient.setName(name);
        currentClient.setSex(sex);
        currentClient.setBirthDate(birthDate);
        currentClient.setPhone(phone);
        currentClient.setHeight(height);
        currentClient.setWeight(weight);
        currentClient.setDistribution(distributionRight);

        viewModel.update(currentClient);

        Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show();
        setEditMode(false);
    }

    private void confirmDelete() {
        if (currentClient == null) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("Delete patient?")
                .setMessage("This will permanently delete the patient. Are you sure?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (d, which) -> {
                    viewModel.delete(currentClient);
                    Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(this).popBackStack();
                })
                .show();
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private int clamp0to100(int v) {
        if (v < 0) return 0;
        if (v > 100) return 100;
        return v;
    }
}