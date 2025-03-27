package com.example.resumebuilder;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.resumebuilder.api.ApiClient;
import com.example.resumebuilder.api.GeminiApiService;
import com.example.resumebuilder.models.ResumeData;
import com.example.resumebuilder.models.Skill;
import com.example.resumebuilder.models.WorkExperience;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for adding skills information to the resume
 */
public class SkillsActivity extends AppCompatActivity {
    
    private LinearLayout skillsContainer;
    private FloatingActionButton fabAddSkill;
    private Button btnNext;
    private Button btnBack;
    private Button btnSave;
    private Button btnSuggestSkills;
    
    private ResumeData resumeData;
    private ArrayList<Skill> skillsList;
    
    private GeminiApiService geminiApiService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skills);
        
        // Set title
        setTitle("Skills");
        
        // Initialize API service
        geminiApiService = ApiClient.getGeminiApiService();
        
        // Get resume data from intent
        if (getIntent() != null && getIntent().hasExtra("resume_data")) {
            resumeData = (ResumeData) getIntent().getSerializableExtra("resume_data");
            
            // Initialize skills list if not already created
            if (resumeData.getSkillsList() != null) {
                skillsList = new ArrayList<>(resumeData.getSkillsList());
            } else {
                skillsList = new ArrayList<>();
            }
        } else {
            // Handle error: no resume data
            Toast.makeText(this, "Error: No resume data found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Initialize UI components
        initUI();
        
        // Display existing skills entries if any
        displaySkillsList();
        
        // Set up listeners
        setupListeners();
    }
    
    private void initUI() {
        skillsContainer = findViewById(R.id.skills_container);
        fabAddSkill = findViewById(R.id.fab_add_skill);
        btnNext = findViewById(R.id.btn_next);
        btnBack = findViewById(R.id.btn_back);
        btnSave = findViewById(R.id.btn_save);
        btnSuggestSkills = findViewById(R.id.btn_suggest_skills);
    }
    
    private void displaySkillsList() {
        skillsContainer.removeAllViews();
        
        if (skillsList.isEmpty()) {
            // Show empty state
            TextView emptyView = new TextView(this);
            emptyView.setText("No skills added yet. Tap + to add your skills or use 'Suggest Skills' to get AI recommendations.");
            emptyView.setPadding(32, 32, 32, 32);
            skillsContainer.addView(emptyView);
        } else {
            // Display each skill entry
            for (int i = 0; i < skillsList.size(); i++) {
                addSkillItemView(skillsList.get(i), i);
            }
        }
    }
    
    private void addSkillItemView(final Skill skill, final int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_skill, null);
        
        // Get references to views
        TextView tvSkillName = view.findViewById(R.id.tv_skill_name);
        TextView tvProficiency = view.findViewById(R.id.tv_proficiency);
        TextView tvCategory = view.findViewById(R.id.tv_category);
        Button btnEdit = view.findViewById(R.id.btn_edit);
        Button btnDelete = view.findViewById(R.id.btn_delete);
        
        // Set data to views
        tvSkillName.setText(skill.getName());
        tvProficiency.setText(skill.getProficiencyText());
        tvCategory.setText(skill.getCategory());
        
        // Set click listeners
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSkillDialog(skill, position);
            }
        });
        
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(position);
            }
        });
        
        // Add to container
        skillsContainer.addView(view);
    }
    
    private void setupListeners() {
        fabAddSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSkillDialog(null, -1);
            }
        });
        
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                // Navigate to summary activity
                Intent intent = new Intent(SkillsActivity.this, SummaryActivity.class);
                intent.putExtra("resume_data", resumeData);
                startActivity(intent);
            }
        });
        
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                saveDraft();
                Toast.makeText(SkillsActivity.this, "Skills information saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        
        btnSuggestSkills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resumeData.getWorkExperienceList() == null || resumeData.getWorkExperienceList().isEmpty()) {
                    Toast.makeText(SkillsActivity.this, "Please add work experience first to get skill suggestions", Toast.LENGTH_SHORT).show();
                } else {
                    suggestSkillsBasedOnExperience();
                }
            }
        });
    }
    
    private void showSkillDialog(final Skill skill, final int position) {
        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_skill, null);
        builder.setView(dialogView);
        
        // Get references to views
        final EditText etSkillName = dialogView.findViewById(R.id.et_skill_name);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final EditText etCategory = dialogView.findViewById(R.id.et_category);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final SeekBar sbProficiency = dialogView.findViewById(R.id.sb_proficiency);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final TextView tvProficiencyText = dialogView.findViewById(R.id.tv_proficiency_text);
        
        final TextInputLayout tilSkillName = dialogView.findViewById(R.id.til_skill_name);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final TextInputLayout tilCategory = dialogView.findViewById(R.id.til_category);
        
        // Set data if editing
        if (skill != null) {
            etSkillName.setText(skill.getName());
            etCategory.setText(skill.getCategory());
            sbProficiency.setProgress(skill.getProficiencyLevel() - 1); // SeekBar is 0-indexed
            updateProficiencyText(tvProficiencyText, skill.getProficiencyLevel());
        } else {
            // Default values for new skill
            sbProficiency.setProgress(2); // Default to Intermediate (3)
            updateProficiencyText(tvProficiencyText, 3);
        }
        
        // Set listener for seekbar
        sbProficiency.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateProficiencyText(tvProficiencyText, progress + 1);
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not needed
            }
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not needed
            }
        });
        
        // Set title
        String title = (skill == null) ? "Add Skill" : "Edit Skill";
        builder.setTitle(title);
        
        // Set buttons
        builder.setPositiveButton("Save", null); // Set listener later to prevent auto-dismiss
        builder.setNegativeButton("Cancel", null);
        
        // Create dialog
        final AlertDialog dialog = builder.create();
        dialog.show();
        
        // Override positive button to validate before dismiss
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate inputs
                boolean isValid = true;
                
                if (etSkillName.getText().toString().trim().isEmpty()) {
                    tilSkillName.setError("Skill name is required");
                    isValid = false;
                } else {
                    tilSkillName.setError(null);
                }
                
                if (etCategory.getText().toString().trim().isEmpty()) {
                    tilCategory.setError("Category is required");
                    isValid = false;
                } else {
                    tilCategory.setError(null);
                }
                
                if (isValid) {
                    // Create or update skill object
                    Skill skillItem = (skill != null) ? skill : new Skill();
                    skillItem.setName(etSkillName.getText().toString().trim());
                    skillItem.setCategory(etCategory.getText().toString().trim());
                    skillItem.setProficiencyLevel(sbProficiency.getProgress() + 1); // SeekBar is 0-indexed
                    
                    // Add or update in list
                    if (position == -1) {
                        skillsList.add(skillItem);
                    } else {
                        skillsList.set(position, skillItem);
                    }
                    
                    // Refresh display
                    displaySkillsList();
                    
                    // Dismiss dialog
                    dialog.dismiss();
                }
            }
        });
    }
    
    private void updateProficiencyText(TextView textView, int proficiencyLevel) {
        String proficiencyText;
        switch (proficiencyLevel) {
            case 1:
                proficiencyText = "Beginner";
                break;
            case 2:
                proficiencyText = "Basic";
                break;
            case 3:
                proficiencyText = "Intermediate";
                break;
            case 4:
                proficiencyText = "Advanced";
                break;
            case 5:
                proficiencyText = "Expert";
                break;
            default:
                proficiencyText = "Intermediate";
                break;
        }
        
        textView.setText("Proficiency: " + proficiencyText + " (" + proficiencyLevel + "/5)");
    }
    
    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete this skill?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                skillsList.remove(position);
                displaySkillsList();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void suggestSkillsBasedOnExperience() {
        // Show loading dialog
        final AlertDialog loadingDialog = new AlertDialog.Builder(this)
                .setTitle("Generating Skill Suggestions")
                .setMessage("Using AI to suggest skills based on your work experience...")
                .setCancelable(false)
                .show();
        
        // Build a string with all work experience descriptions
        StringBuilder experienceText = new StringBuilder();
        for (WorkExperience exp : resumeData.getWorkExperienceList()) {
            experienceText.append("Position: ").append(exp.getPosition()).append("\n");
            experienceText.append("Company: ").append(exp.getCompany()).append("\n");
            experienceText.append("Description: ").append(exp.getDescription()).append("\n\n");
        }
        
        // Prepare the prompt for the Gemini API
        Map<String, Object> content = new HashMap<>();
        Map<String, Object> parts = new HashMap<>();
        
        String prompt = "Based on the following work experience, suggest a comprehensive list of relevant skills " +
                "that should be included in a resume. For each skill, indicate a category (e.g., Technical Skills, " +
                "Soft Skills, Industry Knowledge) and a proficiency level from 1-5.\n\n" +
                "Work Experience:\n" + experienceText.toString() + "\n" +
                "Please format the response as a list with each skill on a new line, followed by the category and " +
                "recommended proficiency level (1-5). For example:\n" +
                "- JavaScript | Technical Skills | 4\n" +
                "- Project Management | Soft Skills | 3";
        
        parts.put("text", prompt);
        
        ArrayList<Object> partsList = new ArrayList<>();
        partsList.add(parts);
        
        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("parts", partsList);
        contentMap.put("role", "user");
        
        ArrayList<Object> contents = new ArrayList<>();
        contents.add(contentMap);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", contents);
        
        // Make API call
        Call<Map<String, Object>> call = new Call<Map< geminiApiService.suggestSkills(requestBody)>
        geminiApiService.suggestSkills(requestBody);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                loadingDialog.dismiss();
                
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Extract the suggested skills from the response
                        Map<String, Object> responseBody = response.body();
                        ArrayList<Object> candidates = (ArrayList<Object>) responseBody.get("candidates");
                        Map<String, Object> candidate = (Map<String, Object>) candidates.get(0);
                        ArrayList<Object> contentArray = (ArrayList<Object>) candidate.get("content");
                        Map<String, Object> content = (Map<String, Object>) contentArray.get(0);
                        ArrayList<Object> partsArray = (ArrayList<Object>) content.get("parts");
                        Map<String, Object> part = (Map<String, Object>) partsArray.get(0);
                        String suggestedSkillsText = (String) part.get("text");
                        
                        // Parse the suggested skills
                        ArrayList<Skill> suggestedSkills = parseSkillsFromText(suggestedSkillsText);
                        
                        if (suggestedSkills.isEmpty()) {
                            Toast.makeText(SkillsActivity.this, "No skills could be extracted from the AI response", Toast.LENGTH_SHORT).show();
                        } else {
                            // Show dialog with suggested skills
                            showSuggestedSkillsDialog(suggestedSkills);
                        }
                    } catch (Exception e) {
                        // Handle parsing error
                        Toast.makeText(SkillsActivity.this, "Error parsing API response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle error response
                    String errorMessage = "Error getting skill suggestions: ";
                    try {
                        errorMessage += response.errorBody().string();
                    } catch (Exception e) {
                        errorMessage += "Unknown error";
                    }
                    
                    Toast.makeText(SkillsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                loadingDialog.dismiss();
                Toast.makeText(SkillsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private ArrayList<Skill> parseSkillsFromText(String text) {
        ArrayList<Skill> skillsList = new ArrayList<>();
        
        // Split by new lines
        String[] lines = text.split("\\n");
        
        // Track existing skill names to avoid duplicates
        Set<String> existingSkillNames = new HashSet<>();
        for (Skill skill : this.skillsList) {
            existingSkillNames.add(skill.getName().toLowerCase());
        }
        
        for (String line : lines) {
            // Look for lines that start with a dash or bullet and contain pipe separators
            if (line.trim().startsWith("-") || line.trim().startsWith("•") || line.trim().startsWith("*")) {
                line = line.trim().substring(1).trim(); // Remove the bullet
                
                // Split by pipe or similar separators
                String[] parts = line.split("\\|");
                
                if (parts.length >= 1) {
                    String skillName = parts[0].trim();
                    
                    // Skip if skill already exists
                    if (existingSkillNames.contains(skillName.toLowerCase())) {
                        continue;
                    }
                    
                    String category = (parts.length >= 2) ? parts[1].trim() : "Technical Skills";
                    
                    int proficiencyLevel = 3; // Default to intermediate
                    if (parts.length >= 3) {
                        try {
                            // Try to parse the proficiency level
                            String profPart = parts[2].trim();
                            if (profPart.length() == 1 && Character.isDigit(profPart.charAt(0))) {
                                proficiencyLevel = Integer.parseInt(profPart);
                            }
                        } catch (NumberFormatException e) {
                            // Keep default
                        }
                    }
                    
                    // Ensure valid proficiency level
                    if (proficiencyLevel < 1) proficiencyLevel = 1;
                    if (proficiencyLevel > 5) proficiencyLevel = 5;
                    
                    Skill skill = new Skill(skillName, proficiencyLevel, category);
                    skillsList.add(skill);
                    existingSkillNames.add(skillName.toLowerCase());
                }
            }
        }
        
        return skillsList;
    }
    
    private void showSuggestedSkillsDialog(final ArrayList<Skill> suggestedSkills) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Suggested Skills");
        
        // Create the list view for skills
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_suggested_skills, null);
        LinearLayout suggestedSkillsContainer = dialogView.findViewById(R.id.suggested_skills_container);
        
        // Add checkboxes for each suggested skill
        final Map<Integer, Boolean> selectedSkills = new HashMap<>();
        
        for (int i = 0; i < suggestedSkills.size(); i++) {
            Skill skill = suggestedSkills.get(i);
            
            View itemView = LayoutInflater.from(this).inflate(R.layout.item_suggested_skill, null);
            TextView tvSkillName = itemView.findViewById(R.id.tv_skill_name);
            TextView tvCategory = itemView.findViewById(R.id.tv_category);
            TextView tvProficiency = itemView.findViewById(R.id.tv_proficiency);
            final View checkBox = itemView.findViewById(R.id.checkbox);
            
            tvSkillName.setText(skill.getName());
            tvCategory.setText(skill.getCategory());
            tvProficiency.setText(skill.getProficiencyText());
            
            // Set up selection behavior
            selectedSkills.put(i, true); // Select all by default
            checkBox.setSelected(true);
            
            final int position = i;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isSelected = !checkBox.isSelected();
                    checkBox.setSelected(isSelected);
                    selectedSkills.put(position, isSelected);
                }
            });
            
            suggestedSkillsContainer.addView(itemView);
        }
        
        builder.setView(dialogView);
        
        // Add buttons
        builder.setPositiveButton("Add Selected", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int addedCount = 0;
                
                // Add selected skills to the list
                for (int i = 0; i < suggestedSkills.size(); i++) {
                    if (selectedSkills.get(i) != null && selectedSkills.get(i)) {
                        skillsList.add(suggestedSkills.get(i));
                        addedCount++;
                    }
                }
                
                // Refresh the display
                displaySkillsList();
                
                // Show confirmation
                Toast.makeText(SkillsActivity.this, addedCount + " skills added to your resume", Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton("Cancel", null);
        
        builder.show();
    }
    
    private void saveData() {
        // Save skills list to resume data
        resumeData.setSkillsList(skillsList);
    }
    
    private void saveDraft() {
        // TODO: Implement persistence logic (SharedPreferences or local database)
    }
    
    @Override
    public void onBackPressed() {
        // Show confirmation dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Go Back");
        builder.setMessage("Are you sure you want to go back? Your changes will be saved.");
        builder.setPositiveButton("Save and Go Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveData();
                SkillsActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}