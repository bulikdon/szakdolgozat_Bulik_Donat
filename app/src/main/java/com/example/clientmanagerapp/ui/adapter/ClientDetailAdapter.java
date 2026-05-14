package com.example.clientmanagerapp.ui.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.clientmanagerapp.ui.fragments.ClientInfoFragment;
import com.example.clientmanagerapp.ui.fragments.ClientTreatmentFragment;

public class ClientDetailAdapter extends FragmentStateAdapter {

    private final int clientId;

    public ClientDetailAdapter(@NonNull Fragment fragment, int clientId) {
        super(fragment);
        this.clientId = clientId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Bundle b = new Bundle();
        b.putInt("clientId", clientId);

        if (position == 0) {
            ClientInfoFragment f = new ClientInfoFragment();
            f.setArguments(b);
            return f;
        } else {
            ClientTreatmentFragment f = new ClientTreatmentFragment();
            f.setArguments(b);
            return f;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
