package com.example.clientmanagerapp.ui.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clientmanagerapp.R;
import com.example.clientmanagerapp.database.entity.Client;
import com.example.clientmanagerapp.ui.fragments.ClientDetailFragment;
import com.example.clientmanagerapp.ui.viewmodel.ClientViewModel;

import java.util.List;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.VH> {

    private List<Client> data;
    private final ClientViewModel viewModel;

    public ClientAdapter(List<Client> data, ClientViewModel viewModel) {
        this.data = data;
        this.viewModel = viewModel;
    }

    public void update(List<Client> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_client, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Client c = data.get(position);

        holder.name.setText(c.getName());

        holder.star.setText(c.isFavorite() ? "★" : "☆");
        holder.star.setOnClickListener(v -> {
            c.setFavorite(!c.isFavorite());
            viewModel.update(c); // kell egy update a DAO-ban
        });

        holder.name.setOnClickListener(v -> {
            Bundle b = new Bundle();
            b.putInt("clientId", c.id);
            androidx.navigation.Navigation.findNavController(v)
                    .navigate(R.id.action_existing_to_clientDetail, b);
        });

    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView name, star;

        VH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textName);
            star = itemView.findViewById(R.id.textStar);
        }
    }
}
