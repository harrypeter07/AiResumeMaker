package com.example.resumebuilder.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.resumebuilder.MainActivity;
import com.example.resumebuilder.R;
import com.example.resumebuilder.utils.PdfGenerator;

import java.io.File;

public class PdfExportActivity extends AppCompatActivity {

    private TextView tvStatus;
    private Button btnShare;
    private Button btnHome;
    private ProgressBar progressBar;
    private String htmlContent;
    private File pdfFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_export);

        // Get HTML content from intent
        htmlContent = getIntent().getStringExtra("html_content");
        if (htmlContent == null || htmlContent.isEmpty()) {
            Toast.makeText(this, "Error: No resume content found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize UI components
        tvStatus = findViewById(R.id.tv_status);
        btnShare = findViewById(R.id.btn_share);
        btnHome = findViewById(R.id.btn_home);
        progressBar = findViewById(R.id.progress_bar);

        // Disable share button until PDF is generated
        btnShare.setEnabled(false);

        // Set up button click listeners
        btnShare.setOnClickListener(v -> sharePdf());

        btnHome.setOnClickListener(v -> {
            // Mark resume as completed
            MainActivity.resumeData.setCompleted(true);
            
            // Return to main activity, clearing the back stack
            Intent intent = new Intent(PdfExportActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Generate PDF
        generatePdf();
    }

    private void generatePdf() {
        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        tvStatus.setText("Generating PDF...");

        // Create a file name based on user's name and job role
        String fileName = MainActivity.resumeData.getFullName().replaceAll("\\s+", "_") + 
                          "_" + MainActivity.resumeData.getJobRole().replaceAll("\\s+", "_") + 
                          "_Resume.pdf";

        // Start PDF generation in a background thread
        new Thread(() -> {
            try {
                // Create PDF generator
                PdfGenerator pdfGenerator = new PdfGenerator(this);
                
                // Generate PDF
                pdfFile = pdfGenerator.generatePdf(htmlContent, fileName);
                
                // Update UI on the main thread
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    if (pdfFile != null && pdfFile.exists()) {
                        tvStatus.setText("PDF generated successfully: " + pdfFile.getName());
                        btnShare.setEnabled(true);
                    } else {
                        tvStatus.setText("Failed to generate PDF. Please try again.");
                    }
                });
            } catch (Exception e) {
                // Handle error
                e.printStackTrace();
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    tvStatus.setText("Error: " + e.getMessage());
                });
            }
        }).start();
    }

    private void sharePdf() {
        if (pdfFile != null && pdfFile.exists()) {
            // Create URI for the PDF file using FileProvider
            Uri pdfUri = FileProvider.getUriForFile(
                    this,
                    getApplicationContext().getPackageName() + ".provider",
                    pdfFile);

            // Create share intent
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            
            // Set a default email subject and text
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, MainActivity.resumeData.getFullName() + " - Resume");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Please find attached my resume for your consideration.");

            // Launch share dialog
            startActivity(Intent.createChooser(shareIntent, "Share Resume PDF"));
        } else {
            Toast.makeText(this, "PDF file not found", Toast.LENGTH_SHORT).show();
        }
    }
}
