package com.example.resumebuilder.utils;

import android.content.Context;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.resumebuilder.models.ResumeData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.UUID;

/**
 * Utility class for generating PDFs from HTML templates
 */
public class PdfGenerator {
    private Context context;
    private TemplateManager templateManager;
    
    /**
     * Constructor for the PdfGenerator
     * @param context Android context
     */
    public PdfGenerator(Context context) {
        this.context = context;
        this.templateManager = new TemplateManager(context);
    }
    
    /**
     * Generate a PDF from resume data
     * @param resumeData The resume data
     * @param jobTitle Job title for the filename
     * @param callback Callback for when the PDF is generated
     */
    public void generatePdf(ResumeData resumeData, String jobTitle, PdfGenerationCallback callback) {
        // Get HTML content from template manager
        String htmlContent = templateManager.getFilledTemplate(resumeData);
        
        // Create temp file for HTML
        File htmlFile = createTempHtmlFile(htmlContent);
        if (htmlFile == null) {
            callback.onPdfGenerationFailed("Failed to create temporary HTML file");
            return;
        }
        
        // Set up WebView for PDF conversion
        final WebView webView = new WebView(context);
        webView.loadUrl("file://" + htmlFile.getAbsolutePath());
        
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                
                // Create print job
                createPrintJob(webView, jobTitle, callback);
                
                // Delete temp HTML file
                htmlFile.delete();
            }
        });
    }
    
    /**
     * Create a print job from a WebView
     * @param webView WebView containing the resume
     * @param jobTitle Job title for the filename
     * @param callback Callback for when the PDF is generated
     */
    private void createPrintJob(WebView webView, String jobTitle, final PdfGenerationCallback callback) {
        // Get print manager
        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
        
        // Create print job name
        String jobName = "Resume_" + (jobTitle != null ? jobTitle.replaceAll("\\s+", "_") : "Default") + "_" + 
                          System.currentTimeMillis();
        
        // Create print adapter
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(jobName);
        
        // Create print job
        printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
        
        // Notify success
        callback.onPdfGenerationSuccess("PDF generated successfully: " + jobName);
    }
    
    /**
     * Create a temporary HTML file
     * @param htmlContent HTML content
     * @return File object for the HTML file
     */
    private File createTempHtmlFile(String htmlContent) {
        try {
            File outputDir = context.getCacheDir();
            File outputFile = File.createTempFile("resume_" + UUID.randomUUID().toString(), ".html", outputDir);
            
            FileOutputStream fos = new FileOutputStream(outputFile);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            writer.write(htmlContent);
            writer.close();
            fos.close();
            
            return outputFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Interface for PDF generation callbacks
     */
    public interface PdfGenerationCallback {
        void onPdfGenerationSuccess(String message);
        void onPdfGenerationFailed(String errorMessage);
    }
}