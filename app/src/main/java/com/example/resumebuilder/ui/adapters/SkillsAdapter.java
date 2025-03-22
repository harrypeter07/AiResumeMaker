package com.example.resumebuilder.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resumebuilder.R;
import com.example.resumebuilder.models.Skill;

import java.util.ArrayList;

public class SkillsAdapter extends RecyclerView.Adapter<SkillsAdapter.SkillViewHolder> {

    private ArrayList<Skill> skillsList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public SkillsAdapter(ArrayList<Skill> skillsList) {
        this.skillsList = skillsList;
    }

    @NonNull
    @Override
    public SkillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_skill, parent, false);
        return new SkillViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull SkillViewHolder holder, int position) {
        Skill skill = skillsList.get(position);
        holder.tvSkillName.setText(skill.getName());
        
        // Set proficiency if available
        if (skill.getProficiency() != null && !skill.getProficiency().isEmpty()) {
            holder.tvProficiency.setVisibility(View.VISIBLE);
            holder.tvProficiency.setText(skill.getProficiency());
        } else {
            holder.tvProficiency.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return skillsList.size();
    }

    public static class SkillViewHolder extends RecyclerView.ViewHolder {
        TextView tvSkillName;
        TextView tvProficiency;
        ImageButton btnDelete;
        ImageButton btnEdit;

        public SkillViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            tvSkillName = itemView.findViewById(R.id.tv_skill_name);
            tvProficiency = itemView.findViewById(R.id.tv_proficiency);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnEdit = itemView.findViewById(R.id.btn_edit);

            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });

            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(position);
                    }
                }
            });
        }
    }
}
