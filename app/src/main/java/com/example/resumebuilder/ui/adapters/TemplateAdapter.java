package com.example.resumebuilder.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resumebuilder.R;
import com.example.resumebuilder.models.Template;

import java.util.ArrayList;

public class TemplateAdapter extends RecyclerView.Adapter<TemplateAdapter.TemplateViewHolder> {

    private ArrayList<Template> templateList;
    private int selectedPosition;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public TemplateAdapter(ArrayList<Template> templateList, int selectedPosition) {
        this.templateList = templateList;
        this.selectedPosition = selectedPosition;
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
    }

    @NonNull
    @Override
    public TemplateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_template, parent, false);
        return new TemplateViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TemplateViewHolder holder, int position) {
        Template template = templateList.get(position);
        holder.templateName.setText(template.getName());
        holder.templateDescription.setText(template.getDescription());
        
        // Load template preview
        holder.templatePreview.loadUrl("file:///android_asset/templates/" + template.getPreviewPath());
        
        // Set selected status
        holder.radioButton.setChecked(position == selectedPosition);
        
        // Set card background based on selection
        if (position == selectedPosition) {
            holder.cardView.setCardBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.colorSelectedBackground));
        } else {
            holder.cardView.setCardBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.colorCardBackground));
        }
    }

    @Override
    public int getItemCount() {
        return templateList.size();
    }

    public static class TemplateViewHolder extends RecyclerView.ViewHolder {
        TextView templateName;
        TextView templateDescription;
        WebView templatePreview;
        RadioButton radioButton;
        CardView cardView;

        public TemplateViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            templateName = itemView.findViewById(R.id.template_name);
            templateDescription = itemView.findViewById(R.id.template_description);
            templatePreview = itemView.findViewById(R.id.template_preview);
            radioButton = itemView.findViewById(R.id.radio_button);
            cardView = itemView.findViewById(R.id.card_view);
            
            // Configure WebView
            templatePreview.getSettings().setJavaScriptEnabled(true);
            templatePreview.getSettings().setLoadWithOverviewMode(true);
            templatePreview.getSettings().setUseWideViewPort(true);
            
            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
            
            // Also set click listener on radio button
            radioButton.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }
}
