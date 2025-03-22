package com.example.resumebuilder.utils;

import android.content.Context;
import android.content.res.AssetManager;

import com.example.resumebuilder.models.Education;
import com.example.resumebuilder.models.ResumeData;
import com.example.resumebuilder.models.Skill;
import com.example.resumebuilder.models.WorkExperience;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Utility class for managing resume templates
 */
public class TemplateManager {
    private Context context;
    private static final String[] TEMPLATE_NAMES = {
            "modern_template.html", 
            "professional_template.html",
            "creative_template.html"
    };
    
    /**
     * Constructor for the TemplateManager
     * @param context Android context
     */
    public TemplateManager(Context context) {
        this.context = context;
    }
    
    /**
     * Get a template by ID
     * @param templateId Template ID (0-2)
     * @return Template HTML content
     */
    public String getTemplate(int templateId) {
        if (templateId < 0 || templateId >= TEMPLATE_NAMES.length) {
            templateId = 0; // Default to modern template
        }
        
        try {
            return readTemplateFromAssets(TEMPLATE_NAMES[templateId]);
        } catch (IOException e) {
            e.printStackTrace();
            return getDefaultTemplate();
        }
    }
    
    /**
     * Read a template from assets
     * @param templateName Template filename
     * @return Template HTML content
     * @throws IOException If there's an error reading the template
     */
    private String readTemplateFromAssets(String templateName) throws IOException {
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("templates/" + templateName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }
        
        reader.close();
        inputStream.close();
        return content.toString();
    }
    
    /**
     * Get a default template if no templates are available
     * @return Default template HTML content
     */
    private String getDefaultTemplate() {
        return "<!DOCTYPE html>\n" +
               "<html>\n" +
               "<head>\n" +
               "    <meta charset=\"UTF-8\">\n" +
               "    <title>Resume</title>\n" +
               "    <style>\n" +
               "        body { font-family: Arial, sans-serif; color: #333; line-height: 1.5; }\n" +
               "        .container { max-width: 800px; margin: 0 auto; padding: 20px; }\n" +
               "        h1 { color: #2a70b8; }\n" +
               "        h2 { color: #2a70b8; border-bottom: 1px solid #eee; padding-bottom: 5px; }\n" +
               "        .section { margin-bottom: 20px; }\n" +
               "        .header { text-align: center; margin-bottom: 30px; }\n" +
               "        .contact-info { text-align: center; margin-bottom: 20px; }\n" +
               "        .education-item, .experience-item { margin-bottom: 15px; }\n" +
               "        .skills-list { display: flex; flex-wrap: wrap; }\n" +
               "        .skill-item { background: #f0f7fd; border-radius: 3px; padding: 5px 10px; margin: 5px; }\n" +
               "    </style>\n" +
               "</head>\n" +
               "<body>\n" +
               "    <div class=\"container\">\n" +
               "        <div class=\"header\">\n" +
               "            <h1>{{FULL_NAME}}</h1>\n" +
               "        </div>\n" +
               "        <div class=\"contact-info\">\n" +
               "            {{EMAIL}} | {{PHONE}} | {{ADDRESS}}\n" +
               "            {{#WEBSITE}}| <a href=\"{{WEBSITE}}\">Website</a>{{/WEBSITE}}\n" +
               "            {{#LINKEDIN}}| <a href=\"{{LINKEDIN}}\">LinkedIn</a>{{/LINKEDIN}}\n" +
               "        </div>\n" +
               "        <div class=\"section\">\n" +
               "            <h2>Summary</h2>\n" +
               "            <p>{{SUMMARY}}</p>\n" +
               "        </div>\n" +
               "        <div class=\"section\">\n" +
               "            <h2>Education</h2>\n" +
               "            {{EDUCATION}}\n" +
               "        </div>\n" +
               "        <div class=\"section\">\n" +
               "            <h2>Experience</h2>\n" +
               "            {{EXPERIENCE}}\n" +
               "        </div>\n" +
               "        <div class=\"section\">\n" +
               "            <h2>Skills</h2>\n" +
               "            <div class=\"skills-list\">\n" +
               "                {{SKILLS}}\n" +
               "            </div>\n" +
               "        </div>\n" +
               "    </div>\n" +
               "</body>\n" +
               "</html>";
    }
    
    /**
     * Fill a template with resume data
     * @param resumeData Resume data
     * @return Filled template HTML content
     */
    public String getFilledTemplate(ResumeData resumeData) {
        
        if (resumeData == null) {
            return "";
        }
        
        String template = getTemplate(resumeData.getSelectedTemplateId());
        
        // Replace personal information placeholders
        template = template.replace("{{FULL_NAME}}", safeguard(resumeData.getFullName()));
        template = template.replace("{{EMAIL}}", safeguard(resumeData.getEmail()));
        template = template.replace("{{PHONE}}", safeguard(resumeData.getPhone()));
        template = template.replace("{{ADDRESS}}", safeguard(resumeData.getAddress()));
        template = template.replace("{{SUMMARY}}", safeguard(resumeData.getSummary()));
        
        // Handle optional fields
        if (resumeData.getWebsite() != null && !resumeData.getWebsite().isEmpty()) {
            template = template.replace("{{#WEBSITE}}", "");
            template = template.replace("{{/WEBSITE}}", "");
            template = template.replace("{{WEBSITE}}", safeguard(resumeData.getWebsite()));
        } else {
            template = template.replaceAll("\\{\\{#WEBSITE\\}\\}.*?\\{\\{/WEBSITE\\}\\}", "");
        }
        
        if (resumeData.getLinkedin() != null && !resumeData.getLinkedin().isEmpty()) {
            template = template.replace("{{#LINKEDIN}}", "");
            template = template.replace("{{/LINKEDIN}}", "");
            template = template.replace("{{LINKEDIN}}", safeguard(resumeData.getLinkedin()));
        } else {
            template = template.replaceAll("\\{\\{#LINKEDIN\\}\\}.*?\\{\\{/LINKEDIN\\}\\}", "");
        }
        
        // Replace education, experience, and skills placeholders
        template = template.replace("{{EDUCATION}}", generateEducationHtml(resumeData.getEducationList()));
        template = template.replace("{{EXPERIENCE}}", generateExperienceHtml(resumeData.getWorkExperienceList()));
        template = template.replace("{{SKILLS}}", generateSkillsHtml(resumeData.getSkillsList()));
        
        return template;
    }
    
    /**
     * Generate HTML for education section
     * @param educationList List of Education objects
     * @return HTML string for education section
     */
    private String generateEducationHtml(List<Education> educationList) {
        if (educationList == null || educationList.isEmpty()) {
            return "<p>No education information provided.</p>";
        }
        
        StringBuilder html = new StringBuilder();
        for (Education education : educationList) {
            html.append("<div class=\"education-item\">");
            html.append("<h3>").append(safeguard(education.getDegree())).append(" in ").append(safeguard(education.getFieldOfStudy())).append("</h3>");
            html.append("<p><strong>").append(safeguard(education.getInstitution())).append("</strong> | ");
            html.append(safeguard(education.getStartDate())).append(" - ").append(safeguard(education.getEndDate())).append("</p>");
            
            if (education.getGpa() != null && !education.getGpa().isEmpty()) {
                html.append("<p>GPA: ").append(safeguard(education.getGpa())).append("</p>");
            }
            
            if (education.getDescription() != null && !education.getDescription().isEmpty()) {
                html.append("<p>").append(safeguard(education.getDescription())).append("</p>");
            }
            
            html.append("</div>");
        }
        
        return html.toString();
    }
    
    /**
     * Generate HTML for work experience section
     * @param workExperienceList List of WorkExperience objects
     * @return HTML string for work experience section
     */
    private String generateExperienceHtml(List<WorkExperience> workExperienceList) {
        if (workExperienceList == null || workExperienceList.isEmpty()) {
            return "<p>No work experience provided.</p>";
        }
        
        StringBuilder html = new StringBuilder();
        for (WorkExperience experience : workExperienceList) {
            html.append("<div class=\"experience-item\">");
            html.append("<h3>").append(safeguard(experience.getPosition())).append(" at ").append(safeguard(experience.getCompany())).append("</h3>");
            html.append("<p><strong>").append(safeguard(experience.getLocation())).append("</strong> | ");
            html.append(safeguard(experience.getStartDate())).append(" - ");
            if (experience.isCurrentJob()) {
                html.append("Present");
            } else {
                html.append(safeguard(experience.getEndDate()));
            }
            html.append("</p>");
            
            if (experience.getDescription() != null && !experience.getDescription().isEmpty()) {
                html.append("<p>").append(safeguard(experience.getDescription())).append("</p>");
            }
            
            html.append("</div>");
        }
        
        return html.toString();
    }
    
    /**
     * Generate HTML for skills section
     * @param skillsList List of Skill objects
     * @return HTML string for skills section
     */
    private String generateSkillsHtml(List<Skill> skillsList) {
        if (skillsList == null || skillsList.isEmpty()) {
            return "<p>No skills provided.</p>";
        }
        
        StringBuilder html = new StringBuilder();
        for (Skill skill : skillsList) {
            html.append("<div class=\"skill-item\">");
            html.append(safeguard(skill.getName()));
            
            // Add proficiency level if available
            if (skill.getProficiencyLevel() > 0) {
                html.append(" (").append(skill.getProficiencyText()).append(")");
            }
            
            html.append("</div>");
        }
        
        return html.toString();
    }
    
    /**
     * Safeguard text to prevent HTML injection
     * @param text Input text
     * @return Sanitized text
     */
    private String safeguard(String text) {
        if (text == null) {
            return "";
        }
        
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#39;");
    }
}