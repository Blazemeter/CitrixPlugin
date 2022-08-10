package com.blazemeter.jmeter.citrix.recorder.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.jmeter.gui.action.template.Template;
import org.apache.jmeter.gui.action.template.TemplateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Updates JMeter TemplateManager with templates in Citrix Plugin bundle.
 *
 * @since 1.0.0
 */
@SuppressWarnings("deprecation")
public class TemplateUpdater {

  private static final Logger LOGGER = LoggerFactory.getLogger(TemplateUpdater.class);
  // com/blazemeter/jmeter/citrix/template/citrixRecordingTemplate.jmx

  // jmeter/bin/templates
  private final File templatesOutputFolder;

  /**
   * @param templatesOutputFolder Templates output folder.
   */
  public TemplateUpdater(File templatesOutputFolder) {
    this.templatesOutputFolder = templatesOutputFolder;
  }

  /**
   * @return true if TemplateManager understands parameterized templates
   */
  public static boolean hasParameterizedTemplate() {
    try {
      TemplateManager.getInstance().getClass().getMethod("parseTemplateFile", File.class);
      return true;
    } catch (NoSuchMethodException ex) {
      return false;
    }
  }

  /**
   * Checks if JMX template and description have changed and updates them on disk
   * Register template to TemplateManager.
   *
   * @param templateName          Template Name  ("BlazeMeter Citrix Recording")
   * @param templateJmxFileName   JMX Template
   * @param descTemplateFileName  File that contains template description
   * @param templatesResourcePath Source folder of templates in jars
   * @throws IOException when writing template to file fails
   */
  public void addTemplate(
      String templateName, // "BlazeMeter Citrix Recording"
      String templateJmxFileName, // citrixRecordingTemplate.jmx
      String descTemplateFileName, // bzmCitrixTemplate.xml
      String templatesResourcePath // com/blazemeter/jmeter/citrix/template
  ) throws IOException {
    LOGGER.info("Checking if we need to refresh XML template file {}/{}", templatesOutputFolder,
        descTemplateFileName);
    if (mustWriteResource(templatesOutputFolder, templatesResourcePath, descTemplateFileName)) {
      LOGGER.info("Writing file to {}/{}", templatesOutputFolder, descTemplateFileName);
      writeResourceToFile(templatesOutputFolder, templatesResourcePath, descTemplateFileName);
    }
    LOGGER.info("Checking if we need to refresh JMX template file {}/{}", templatesOutputFolder,
        templateJmxFileName);
    if (mustWriteResource(templatesOutputFolder, templatesResourcePath, templateJmxFileName)) {
      LOGGER.info("Writing file to {}/{}", templatesOutputFolder, templateJmxFileName);
      writeResourceToFile(templatesOutputFolder, templatesResourcePath, templateJmxFileName);
    }

    if (!templateExists(templateName)) {
      Template template;
      File templateFile = new File(templatesOutputFolder, descTemplateFileName);
      try {
        Method parseTemplate =
            TemplateManager.getInstance().getClass().getMethod("parseTemplateFile", File.class);
        Map<String, Template> mapTemplates =
            (Map<String, Template>) parseTemplate.invoke(TemplateManager.getInstance(),
                templateFile);
        template = mapTemplates.get(templateName);
        LOGGER.info("Registering template {}", template);
        TemplateManager.getInstance().addTemplate(template);
      } catch (NoSuchMethodException ex) {
        String templateDescription;
        templateDescription =
            extractDescriptionTag(templatesResourcePath + "/" + descTemplateFileName,
                getResourceAsString(templatesResourcePath + "/" + descTemplateFileName));
        template = new Template();
        template.setDescription(templateDescription);
        template.setName(templateName);
        template.setFileName(templateJmxFileName);
        template.setParent(templatesOutputFolder);
        template.setTestPlan(true);
        LOGGER.info("Registering template {}", template);
        TemplateManager.getInstance().addTemplate(template);
      } catch (Exception ex) {
        LOGGER.error("Error parsing template file {}", templateFile, ex);
      }
    }
  }

  private String extractDescriptionTag(String resourcePath, String resourceAsString) {
    final String text = "<description><![CDATA[";
    int startIndex = resourceAsString.indexOf("<description><![CDATA[");
    int endIndex = resourceAsString.indexOf("]]></description>");
    if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
      return StringEscapeUtils
          .unescapeXml(resourceAsString.substring(startIndex + text.length(), endIndex));
    } else {
      throw new IllegalArgumentException("No description found in " + resourcePath);
    }
  }

  private boolean mustWriteResource(File outputFolder,
                                    String templatesResourcePath, String resourceName)
      throws IOException {
    File dest = new File(outputFolder, resourceName);
    return (!dest.exists() || hasFileChanged(templatesResourcePath + "/" + resourceName, dest));
  }

  private void writeResourceToFile(File outputFolder,
                                   String templatesResourcePath,
                                   String resourceName) throws IOException {
    File dest = new File(outputFolder, resourceName);
    try (FileOutputStream fileOutputStream =
             new FileOutputStream(dest);
         OutputStreamWriter outputStreamWriter =
             new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8)) {
      outputStreamWriter.write(getResourceAsString(templatesResourcePath + "/" + resourceName));
    }
  }

  private String getFileAsString(File file) throws IOException {
    try (InputStream inputStream = new FileInputStream(file)) {
      return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    }
  }

  private String getResourceAsString(String fileName) throws IOException {
    try (InputStream inputStream = TemplateUpdater.class.getResourceAsStream(fileName)) {
      return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    }
  }

  private boolean templateExists(String templateName) {
    return TemplateManager.getInstance().getTemplateByName(templateName) != null;
  }

  private boolean hasFileChanged(String templateResourcePath, File currentTemplateFile)
      throws IOException {
    String currentFileContent = getFileAsString(currentTemplateFile);
    String pluginFileContent = getResourceAsString(templateResourcePath);
    return !currentFileContent.equals(pluginFileContent);
  }
}
