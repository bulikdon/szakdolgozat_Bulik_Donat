package com.example.clientmanagerapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clientmanagerapp.R;
import com.example.clientmanagerapp.database.entity.Treatment;
import com.example.clientmanagerapp.ui.viewmodel.PainMapAbsPreviewView;
import com.example.clientmanagerapp.ui.viewmodel.PainMapFootPreviewView;
import com.example.clientmanagerapp.ui.viewmodel.PainMapFullBodyPreviewView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TreatmentAdapter extends RecyclerView.Adapter<TreatmentAdapter.VH> {

    private final List<Treatment> items = new ArrayList<>();
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_treatment, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Treatment t = items.get(position);

        holder.textType.setText(t.type);
        holder.textDate.setText(df.format(new Date(t.timestamp)));

        String notes = (t.notes == null) ? "" : t.notes.trim();
        holder.textNotes.setText(notes.isEmpty() ? "(no notes)" : notes);

        // ✅ alapból mindent elrejtünk
        holder.previewFullBody.setVisibility(View.GONE);
        holder.previewAbs.setVisibility(View.GONE);

        // ✅ csak a megfelelő preview-t mutatjuk + töltjük fel
        if ("FULL_BODY".equals(t.type)) {
            holder.previewFullBody.setVisibility(View.VISIBLE);
            holder.previewFullBody.setPainMapJson(t.painMapJson);
        } else if ("ABS".equals(t.type)) {
            holder.previewAbs.setVisibility(View.VISIBLE);
            holder.previewAbs.setPainMapJson(t.painMapJson);
        } else if ("FOOT".equals(t.type)) {
            holder.previewFoot.setVisibility(View.VISIBLE);
            holder.previewFoot.setPainMapJson(t.painMapJson);
    }
        // ide jöhet majd:
        // else if ("BACK".equals(t.type)) { ... }
        // else if ("SHOULDER".equals(t.type)) { ... }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void submit(List<Treatment> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView textType, textDate, textNotes;
        PainMapFullBodyPreviewView previewFullBody;
        PainMapAbsPreviewView previewAbs;
        PainMapFootPreviewView previewFoot;

        VH(@NonNull View itemView) {
            super(itemView);
            textType = itemView.findViewById(R.id.textType);
            textDate = itemView.findViewById(R.id.textDate);
            textNotes = itemView.findViewById(R.id.textNotes);

            // ⚠️ ezeknek az ID-knak egyezniük kell az item_treatment.xml-lel
            previewFullBody = itemView.findViewById(R.id.painPreview); // FULL_BODY preview
            previewAbs = itemView.findViewById(R.id.absPreview);      // ABS preview
            previewFoot = itemView.findViewById(R.id.footPreview);
        }
    }
}