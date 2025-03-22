package com.example.resumebuilder.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resumebuilder.R;
import com.example.resumebuilder.models.WorkExperience;

import java.util.ArrayList;

public class WorkExperienceAdapter extends RecyclerView.Adapter<WorkExperienceAdapter.WorkExperienceViewHolder> {

    private ArrayList<WorkExperience> workExperienceList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public WorkExperienceAdapter(ArrayList<WorkExperience> workExperienceList) {
        this.workExperienceList = workExperienceList;
    }

    @NonNull
    @Override
    public WorkExperienceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_work_experience, parent, false);
        return new WorkExperienceViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkExperienceViewHolder holder, int position) {
        WorkExperience workExperience = workExperienceList.get(position);
        holder.tvCompany.setText(workExperience.getCompany());
        holder.tvPosition.setText(workExperience.getPosition());
        
        // Format date range
        String dateRange = workExperience.getStartDate();
        if (workExperience.getEndDate() != null && !workExperience.getEndDate().isEmpty()) {
            dateRange += " - " + workExperience.getEndDate();
        } else {
            dateRange += " - Present";
        }
        holder.tvDateRange.setText(dateRange);
        
        // Set description if available
        if (workExperience.getDescription() != null && !workExperience.getDescription().isEmpty()) {
            holder.tvDescription.setVisibility(View.VISIBLE);
            holder.tvDescription.setText(workExperience.getDescription());
        } else {
            holder.tvDescription.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return workExperienceList.size();
    }

    public static class WorkExperienceViewHolder extends RecyclerView.ViewHolder {
        TextView tvCompany;
        TextView tvPosition;
        TextView tvDateRange;
        TextView tvDescription;
        ImageButton btnDelete;
        ImageButton btnEdit;

        public WorkExperienceViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            tvCompany = itemView.findViewById(R.id.tv_company);
            tvPosition = itemView.findViewById(R.id.tv_position);
            tvDateRange = itemView.findViewById(R.id.tv_date_range);
            tvDescription = itemView.findViewById(R.id.tv_description);
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
