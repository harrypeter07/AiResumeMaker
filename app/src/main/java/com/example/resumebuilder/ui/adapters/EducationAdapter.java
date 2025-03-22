package com.example.resumebuilder.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resumebuilder.R;
import com.example.resumebuilder.models.Education;

import java.util.ArrayList;

public class EducationAdapter extends RecyclerView.Adapter<EducationAdapter.EducationViewHolder> {

    private ArrayList<Education> educationList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public EducationAdapter(ArrayList<Education> educationList) {
        this.educationList = educationList;
    }

    @NonNull
    @Override
    public EducationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_education, parent, false);
        return new EducationViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull EducationViewHolder holder, int position) {
        Education education = educationList.get(position);
        holder.tvDegree.setText(education.getDegree());
        holder.tvInstitution.setText(education.getInstitution());
        holder.tvYear.setText(education.getYear());
        holder.tvGrade.setText(education.getGrade());
    }

    @Override
    public int getItemCount() {
        return educationList.size();
    }

    public static class EducationViewHolder extends RecyclerView.ViewHolder {
        TextView tvDegree;
        TextView tvInstitution;
        TextView tvYear;
        TextView tvGrade;
        ImageButton btnDelete;
        ImageButton btnEdit;

        public EducationViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            tvDegree = itemView.findViewById(R.id.tv_degree);
            tvInstitution = itemView.findViewById(R.id.tv_institution);
            tvYear = itemView.findViewById(R.id.tv_year);
            tvGrade = itemView.findViewById(R.id.tv_grade);
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
