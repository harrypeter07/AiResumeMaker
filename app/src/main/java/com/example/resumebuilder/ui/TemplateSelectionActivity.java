package com.example.resumebuilder.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resumebuilder.MainActivity;
import com.example.resumebuilder.R;
import com.example.resumebuilder.models.Template;
import com.example.resumebuilder.ui.adapters.TemplateAdapter;
import com.example.resumebuilder.utils.TemplateManager;

import java.util.ArrayList;

public class TemplateSelectionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TemplateAdapter adapter;
    private ArrayList<Template> templateList;
    private Button btnNext;
    private Button btnBack;
    private int selectedTemplatePosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_selection);

        // Load templates
        TemplateManager templateManager = new TemplateManager(this);
        templateList = templateManager.getAvailableTemplates();
        
        // Initialize UI components
        recyclerView = findViewById(R.id.recycler_templates);
        btnNext = findViewById(R.id.btn_next);
        btnBack = findViewById(R.id.btn_back);
        
        // If template is already selected, set the position
        if (MainActivity.resumeData.getSelectedTemplate() != null) {
            for (int i = 0; i < templateList.size(); i++) {
                if (templateList.get(i).getId().equals(MainActivity.resumeData.getSelectedTemplate().getId())) {
                    selectedTemplatePosition = i;
                    break;
                }
            }
        }
        
        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TemplateAdapter(templateList, selectedTemplatePosition);
        recyclerView.setAdapter(adapter);
        
        // Set up item click listener
        adapter.setOnItemClickListener(new TemplateAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Update selected position
                selectedTemplatePosition = position;
                adapter.setSelectedPosition(position);
                adapter.notifyDataSetChanged();
            }
        });
        
        // Set up button click listeners
        btnNext.setOnClickListener(v -> {
            if (selectedTemplatePosition == -1) {
                Toast.makeText(TemplateSelectionActivity.this, "Please select a template", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Save selected template to resume data
            MainActivity.resumeData.setSelectedTemplate(templateList.get(selectedTemplatePosition));
            
            // Navigate to resume preview
            Intent intent = new Intent(TemplateSelectionActivity.this, ResumePreviewActivity.class);
            startActivity(intent);
        });
        
        btnBack.setOnClickListener(v -> finish());
    }
}
