package com.example.resumebuilder.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.resumebuilder.MainActivity;
import com.example.resumebuilder.R;
import com.example.resumebuilder.utils.TemplateManager;

public class ResumePreviewActivity extends AppCompatActivity {

    private WebView webView;
    private Button btnExport;
    private Button btnBack;
    private Button btnEdit;
    private ProgressBar progressBar;
    private TemplateManager templateManager;
    private String htmlContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_preview);

        // Initialize UI components
        webView = findViewById(R.id.web_view);
        btnExport = findViewById(R.id.btn_export);
        btnBack = findViewById(R.id.btn_back);
        btnEdit = findViewById(R.id.btn_edit);
        progressBar = findViewById(R.id.progress_bar);

        // Initialize template manager
        templateManager = new TemplateManager(this);

        // Configure WebView
        // Configure WebView with security settings
        webView.getSettings().setJavaScriptEnabled(false);
        webView.getSettings().setAllowFileAccess(false);
        webView.getSettings().setCacheMode(android.webkit.WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        // Generate HTML content from selected template and resume data
        progressBar.setVisibility(View.VISIBLE);
        generateResumeHTML();

        // Set up button click listeners
        btnExport.setOnClickListener(v -> {
            // Navigate to PDF export activity
            Intent intent = new Intent(ResumePreviewActivity.this, PdfExportActivity.class);
            intent.putExtra("html_content", htmlContent);
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> finish());

        btnEdit.setOnClickListener(v -> {
            // Show dialog to select which section to edit
            showEditOptionsDialog();
        });
    }

    private void generateResumeHTML() {
        // Check if we have all the necessary data
        if (MainActivity.resumeData.getSelectedTemplate() == null) {
            Toast.makeText(this, "No template selected. Please go back and select a template.", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        // Generate HTML content from selected template and resume data
        htmlContent = templateManager.generateResumeHTML(MainActivity.resumeData);

        // Load HTML content into WebView
        webView.loadDataWithBaseURL("file:///android_asset/", htmlContent, "text/html", "UTF-8", null);

        // Hide progress bar when content is loaded
        webView.setWebViewClient(new android.webkit.WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showEditOptionsDialog() {
        // Create dialog with edit options
        String[] editOptions = {
                "Personal Details",
                "Education",
                "Work Experience",
                "Skills",
                "Summary",
                "Change Template"
        };

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Edit Resume");
        builder.setItems(editOptions, (dialog, which) -> {
            // Navigate to the appropriate activity based on the selection
            Intent intent;
            switch (which) {
                case 0: // Personal Details
                    intent = new Intent(ResumePreviewActivity.this, UserDetailsActivity.class);
                    break;
                case 1: // Education
                    intent = new Intent(ResumePreviewActivity.this, EducationActivity.class);
                    break;
                case 2: // Work Experience
                    intent = new Intent(ResumePreviewActivity.this, WorkExperienceActivity.class);
                    break;
                case 3: // Skills
                    intent = new Intent(ResumePreviewActivity.this, SkillsActivity.class);
                    break;
                case 4: // Summary
                    intent = new Intent(ResumePreviewActivity.this, SummaryActivity.class);
                    break;
                case 5: // Change Template
                    intent = new Intent(ResumePreviewActivity.this, TemplateSelectionActivity.class);
                    break;
                default:
                    return;
            }
            startActivity(intent);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Regenerate HTML content when returning to this activity
        progressBar.setVisibility(View.VISIBLE);
        generateResumeHTML();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.clearHistory();
            webView.clearCache(true);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
