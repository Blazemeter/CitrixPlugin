package com.blazemeter.jmeter.citrix.listener;

import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.engine.util.NoThreadClone;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.util.JMeterStopTestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Save ICA file response to file and export path as variable.
 */
public class CitrixIcaFileSaver extends AbstractTestElement
    implements NoThreadClone, Serializable, SampleListener, TestStateListener {
  public static final String SAVE_FOLDER = "CitrixIcaFileSaver.outputfolder"; // $NON-NLS-1$
  public static final String VARIABLE_NAME = "CitrixIcaFileSaver.variablename"; // $NON-NLS-1$
  public static final String DEFAULT_ICA_FOLDER_NAME =
      JMeterUtils.getPropDefault(CitrixUtils.PROPERTIES_PFX + "ica_files_folder",
          JMeterUtils.getJMeterHome() + File.separatorChar + "ica_files");
  private static final String ICA_FILE_SUFFIX = ".ica";

  //+ JMX property names; do not change
  /**
   *
   */
  private static final long serialVersionUID = -1123403766809274305L;
  private static final String ICA_CONTENT_TYPE_PREFIX = "application/x-ica";
  private static final Logger LOG = LoggerFactory.getLogger(CitrixIcaFileSaver.class);
  private static final String DEFAULT_VARIABLE_NAME = "citrix_ica_file";
  public static final String DEFAULT_ICA_FILE_PATH_VAR =
      JMeterUtils.getPropDefault(CitrixUtils.PROPERTIES_PFX + "ica_file_path_var",
          DEFAULT_VARIABLE_NAME);

  public CitrixIcaFileSaver() {
    super();
  }

  /**
   * Constructor for use during startup (intended for non-GUI use).
   *
   * @param name of summariser
   */
  public CitrixIcaFileSaver(String name) {
    this();
    setName(name);
  }

  /**
   * Saves the sample result (and any sub results) in files.
   *
   * @see SampleListener#sampleOccurred(SampleEvent)
   */
  @Override
  public void sampleOccurred(SampleEvent e) {
    processSample(e.getResult());
  }

  /**
   * Recurse the whole (sub)result hierarchy.
   *
   * @param s Sample result
   */
  private void processSample(SampleResult s) {
    saveSample(s);
    SampleResult[] sampleResults = s.getSubResults();
    for (SampleResult sampleResult : sampleResults) {
      processSample(sampleResult);
    }
  }

  /**
   * @param s SampleResult to save
   */
  private void saveSample(SampleResult s) {
    if (isIcaFileSampleResult(s)) {
      String sampleLabel = s.getSampleLabel();
      String fileName = UUID.randomUUID().toString() + ICA_FILE_SUFFIX;
      File out = new File(getSaveFolder(), fileName);
      if (LOG.isDebugEnabled()) {
        LOG.debug("Saving ICA file to {} in {}", out.getAbsolutePath(), sampleLabel);
      }
      s.setResultFileName(fileName); // Associate sample with file name
      String variable = getVariableName();

      try (FileOutputStream fos = new FileOutputStream(out);
           BufferedOutputStream bos = new BufferedOutputStream(fos)) {
        // Should we read InputEncoding field to decide in which format to save it ?
        IOUtils.write(s.getResponseDataAsString(), bos, StandardCharsets.UTF_8);
        if (LOG.isDebugEnabled()) {
          LOG.debug("Saved ICA file to {} in {}, storing path in variable {}",
              out.getAbsolutePath(), sampleLabel,
              variable);
        }
        JMeterContextService.getContext().getVariables().put(variable, out.getAbsolutePath());
      } catch (FileNotFoundException e) {
        LOG.error("Error creating sample file for {}", sampleLabel, e);
      } catch (IOException e) {
        LOG.error("Error saving sample {}", sampleLabel, e);
      }
    }
  }

  private boolean isIcaFileSampleResult(SampleResult s) {
    String contentType = s.getContentType().toLowerCase();
    return (!StringUtils.isEmpty(contentType) && contentType.startsWith(ICA_CONTENT_TYPE_PREFIX));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void sampleStarted(SampleEvent e) {
    // not used
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void sampleStopped(SampleEvent e) {
    // not used
  }

  public String getSaveFolder() {
    return getPropertyAsString(SAVE_FOLDER, DEFAULT_ICA_FOLDER_NAME);
  }

  public void setSaveFolder(String value) {
    setProperty(SAVE_FOLDER, value);
  }

  public String getVariableName() {
    return getPropertyAsString(VARIABLE_NAME, DEFAULT_ICA_FILE_PATH_VAR); // $NON-NLS-1$
  }

  public void setVariableName(String value) {
    setProperty(VARIABLE_NAME, value); //$NON-NLS-1$
  }

  @Override
  public void testStarted() {
    testStarted("");
  }

  @Override
  public void testStarted(String host) {
    String elementName = getName();
    File icaSaveFolder = new File(getSaveFolder());
    String icaSaveFolderPath = icaSaveFolder.getAbsolutePath();
    LOG.info("{} checking ICA save folder {}", elementName, icaSaveFolderPath);
    if (!icaSaveFolder.exists()) {
      LOG.info("{} will create ICA save folder {} as it does not exist, creating", elementName,
          icaSaveFolderPath);
      if (!icaSaveFolder.mkdirs()) {
        throw new JMeterStopTestException(
            "Element " + elementName + " failed to created ICA save folder " + icaSaveFolderPath);
      }
    }
    if (!(icaSaveFolder.exists() && icaSaveFolder.canWrite())) {
      throw new JMeterStopTestException(
          "Element " + elementName + " ICA save folder " + icaSaveFolderPath
              + " is not usabled (exists=" + icaSaveFolder.exists() + ", canWrite=" +
              icaSaveFolder.canWrite());
    }
    LOG.info("{} ICA save folder {} is ok", elementName, icaSaveFolderPath);

    LOG.info("{} cleaning old ICA files in {}", elementName, icaSaveFolderPath);
    cleanupOldIcaFiles(icaSaveFolder);
  }

  /**
   * Delete old ica files in icaSaveFolder.
   *
   * @param icaSaveFolder Folder where ICA files are saved
   */
  private void cleanupOldIcaFiles(File icaSaveFolder) {
    File[] filesToDelete = icaSaveFolder.listFiles(
        (File dir, String name) -> (name.endsWith(".ica") && new File(dir, name).isFile()));
    for (File file : filesToDelete) {
      LOG.debug("Deleting old ica file:{}", file);
      if (file.delete()) {
        LOG.debug("Deleted old ica file:{}", file);
      } else {
        LOG.warn("Could not delete old ica file:{}", file);
      }
    }
  }

  @Override
  public void testEnded() {
    // NOOP
  }

  @Override
  public void testEnded(String host) {
    // NOOP
  }
}
