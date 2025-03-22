package com.example.resumebuilder.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resumebuilder.MainActivity;
import com.example.resumebuilder.R;
import com.example.resumebuilder.models.Skill;
import com.example.resumebuilder.ui.adapters.SkillsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class SkillsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SkillsAdapter adapter;
    private ArrayList<Skill> skillsList;
    private FloatingActionButton fabAddSkill;
    private Button btnNext;
    private Button btnBack;
    private Button btnGenerateSkills;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skills);

        // Initialize the skills list
        if (MainActivity.resumeData.getSkillsList() != null) {
            skillsList = MainActivity.resumeData.getSkillsList();
        } else {
            skillsList = new ArrayList<>();
        }

        // Initialize UI components
        recyclerView = findViewById(R.id.recycler_skills);
        fabAddSkill = findViewById(R.id.fab_add_skill);
        btnNext = findViewById(R.id.btn_next);
        btnBack = findViewById(R.id.btn_back);
        btnGenerateSkills = findViewById(R.id.btn_generate_skills);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SkillsAdapter(skillsList);
        recyclerView.setAdapter(adapter);

        // Set up click listeners
        fabAddSkill.setOnClickListener(v -> showAddSkillDialog());

        btnNext.setOnClickListener(v -> {
            // Save skills list to resume data
            MainActivity.resumeData.setSkillsList(skillsList);
            // Navigate to summary activity
            Intent intent = new Intent(SkillsActivity.this, SummaryActivity.class);
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> finish());
        
        btnGenerateSkills.setOnClickListener(v -> {
            // Show loading indicator
            Toast.makeText(SkillsActivity.this, "Generating skills suggestions...", Toast.LENGTH_SHORT).show();
            // Call Gemini API to generate skills based on job role and experience
            generateSkillsWithAI();
        });
        
        // Setup item click for editing
        adapter.setOnItemClickListener(new SkillsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showEditSkillDialog(position);
            }
            
            @Override
            public void onDeleteClick(int position) {
                removeSkillItem(position);
            }
        });
    }

    private void showAddSkillDialog() {
        // Create dialog for adding skill
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_skill, null);
        builder.setView(dialogView);
        builder.setTitle("Add Skill");

        final TextInputEditText etSkillName = dialogView.findViewById(R.id.et_skill_name);
        final TextInputEditText etProficiency = dialogView.findViewById(R.id.et_proficiency);

        builder.setPositiveButton("Add", (dialog, which) -> {
            // Validate inputs
            String skillName = etSkillName.getText().toString().trim();
            String proficiency = etProficiency.getText().toString().trim();

            if (skillName.isEmpty()) {
                Toast.makeText(SkillsActivity.this, "Skill name is required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add skill to the list
            Skill skill = new Skill(skillName, proficiency);
            skillsList.add(skill);
            adapter.notifyItemInserted(skillsList.size() - 1);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void showEditSkillDialog(int position) {
        // Create dialog for editing skill
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_skill, null);
        builder.setView(dialogView);
        builder.setTitle("Edit Skill");

        final TextInputEditText etSkillName = dialogView.findViewById(R.id.et_skill_name);
        final TextInputEditText etProficiency = dialogView.findViewById(R.id.et_proficiency);

        // Pre-fill the fields with existing data
        Skill skill = skillsList.get(position);
        etSkillName.setText(skill.getName());
        etProficiency.setText(skill.getProficiency());

        builder.setPositiveButton("Save", (dialog, which) -> {
            // Validate inputs
            String skillName = etSkillName.getText().toString().trim();
            String proficiency = etProficiency.getText().toString().trim();

            if (skillName.isEmpty()) {
                Toast.makeText(SkillsActivity.this, "Skill name is required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update skill in the list
            skill.setName(skillName);
            skill.setProficiency(proficiency);
            adapter.notifyItemChanged(position);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void removeSkillItem(int position) {
        // Ask for confirmation before removing
        new AlertDialog.Builder(this)
                .setTitle("Delete Skill")
                .setMessage("Are you sure you want to delete this skill?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    skillsList.remove(position);
                    adapter.notifyItemRemoved(position);
                })
                .setNegativeButton("No", null)
                .show();
    }
    
    private void generateSkillsWithAI() {
        // TODO: Implement AI skill generation with Gemini API
        // For now, we'll add some placeholder skills based on the job role
        
        String jobRole = MainActivity.resumeData.getJobRole();
        if (jobRole == null || jobRole.isEmpty()) {
            Toast.makeText(this, "Please set a job role first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show a dialog to confirm adding the generated skills
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Generated Skills?");
        
        // Simulated skills based on common job roles
        ArrayList<Skill> suggestedSkills = new ArrayList<>();
        
        if (jobRole.toLowerCase().contains("software") || jobRole.toLowerCase().contains("developer")) {
            suggestedSkills.add(new Skill("Java", "Advanced"));
            suggestedSkills.add(new Skill("Python", "Intermediate"));
            suggestedSkills.add(new Skill("Git", "Advanced"));
            suggestedSkills.add(new Skill("Agile Development", "Intermediate"));
            suggestedSkills.add(new Skill("Problem Solving", "Advanced"));
        } else if (jobRole.toLowerCase().contains("design") || jobRole.toLowerCase().contains("ui") || 
                   jobRole.toLowerCase().contains("ux")) {
            suggestedSkills.add(new Skill("Adobe XD", "Advanced"));
            suggestedSkills.add(new Skill("Figma", "Advanced"));
            suggestedSkills.add(new Skill("UI/UX Design", "Advanced"));
            suggestedSkills.add(new Skill("Wireframing", "Intermediate"));
            suggestedSkills.add(new Skill("User Research", "Intermediate"));
        } else if (jobRole.toLowerCase().contains("market") || jobRole.toLowerCase().contains("sales")) {
            suggestedSkills.add(new Skill("Digital Marketing", "Advanced"));
            suggestedSkills.add(new Skill("SEO", "Intermediate"));
            suggestedSkills.add(new Skill("Social Media Marketing", "Advanced"));
            suggestedSkills.add(new Skill("Content Creation", "Intermediate"));
            suggestedSkills.add(new Skill("Analytics", "Intermediate"));
        } else {
            suggestedSkills.add(new Skill("Communication", "Advanced"));
            suggestedSkills.add(new Skill("Leadership", "Intermediate"));
            suggestedSkills.add(new Skill("Time Management", "Advanced"));
            suggestedSkills.add(new Skill("Problem Solving", "Intermediate"));
            suggestedSkills.add(new Skill("Teamwork", "Advanced"));
        }
        
        // Create a string with the suggested skills
        StringBuilder skillsText = new StringBuilder();
        for (Skill skill : suggestedSkills) {
            skillsText.append("• ").append(skill.getName());
            if (!skill.getProficiency().isEmpty()) {
                skillsText.append(" (").append(skill.getProficiency()).append(")");
            }
            skillsText.append("\n");
        }
        
        builder.setMessage("Here are some suggested skills for " + jobRole + ":\n\n" + skillsText.toString() + 
                           "\nWould you like to add these skills to your resume?");
        
        builder.setPositiveButton("Add All", (dialog, which) -> {
            // Add all suggested skills to the list
            for (Skill skill : suggestedSkills) {
                if (!containsSkill(skillsList, skill.getName())) {
                    skillsList.add(skill);
                }
            }
            adapter.notifyDataSetChanged();
            Toast.makeText(SkillsActivity.this, "Skills added successfully", Toast.LENGTH_SHORT).show();
        });
        
        builder.setNeutralButton("Add Selected", (dialog, which) -> {
            // Show a multi-choice dialog to select skills
            showSkillSelectionDialog(suggestedSkills);
        });
        
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        
        builder.create().show();
    }
    
    private boolean containsSkill(ArrayList<Skill> skillsList, String skillName) {
        for (Skill skill : skillsList) {
            if (skill.getName().equalsIgnoreCase(skillName)) {
                return true;
            }
        }
        return false;
    }
    
    private void showSkillSelectionDialog(ArrayList<Skill> suggestedSkills) {
        // Create multi-choice dialog for skill selection
        String[] skillNames = new String[suggestedSkills.size()];
        boolean[] checkedItems = new boolean[suggestedSkills.size()];
        
        for (int i = 0; i < suggestedSkills.size(); i++) {
            skillNames[i] = suggestedSkills.get(i).getName() + " (" + suggestedSkills.get(i).getProficiency() + ")";
            checkedItems[i] = true; // Default all to selected
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Skills to Add");
        builder.setMultiChoiceItems(skillNames, checkedItems, (dialog, which, isChecked) -> {
            checkedItems[which] = isChecked;
        });
        
        builder.setPositiveButton("Add Selected", (dialog, which) -> {
            int addedCount = 0;
            for (int i = 0; i < checkedItems.length; i++) {
                if (checkedItems[i]) {
                    Skill skill = suggestedSkills.get(i);
                    if (!containsSkill(skillsList, skill.getName())) {
                        skillsList.add(skill);
                        addedCount++;
                    }
                }
            }
            adapter.notifyDataSetChanged();
            Toast.makeText(SkillsActivity.this, addedCount + " skills added successfully", Toast.LENGTH_SHORT).show();
        });
        
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        
        builder.create().show();
    }
}
