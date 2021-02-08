package com.blazemeter.jmeter.citrix.sampler;

import static com.blazemeter.jmeter.citrix.sampler.SamplerRunException.ErrorCode;

import com.blazemeter.jmeter.citrix.client.CitrixClient;
import com.blazemeter.jmeter.citrix.client.CitrixClientException;
import com.blazemeter.jmeter.citrix.client.events.SessionEvent;
import com.blazemeter.jmeter.citrix.client.events.SessionEvent.EventType;
import com.blazemeter.jmeter.citrix.ocr.OcrManagerHolder;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import java.nio.file.Paths;
import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.testelement.TestIterationListener;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.testelement.ThreadListener;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Special sampler in charge of starting remote application.
 */
public class StartApplicationSampler extends CitrixBaseSampler
    implements ThreadListener, TestStateListener, TestIterationListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(StartApplicationSampler.class);

  private static final boolean FORCE_NORMAL_MODE =
      JMeterUtils.getPropDefault(CitrixUtils.PROPERTIES_PFX + "force_normal_output", false);
  // $NON-NLS-1$
  private static final boolean KEEP_ICA_FILES =
      JMeterUtils.getPropDefault(CitrixUtils.PROPERTIES_PFX + "keep_ica_files", false);
  // $NON-NLS-1$

  private static final String FILE_PATH_VAR_PROP = "StartApplicationSampler.filePathVar";

  /**
   * Instantiates a new {@link StartApplicationSampler}.
   * <p>
   * This sampler requires that no Citrix client is already running
   */
  public StartApplicationSampler() {
    super(RunningClientPolicy.FORBIDDEN);
  }

  @Override
  public void initClient(CitrixClient client) {
    super.initClient(client);
  }

  public String getICAPathVar() {
    return getPropertyAsString(FILE_PATH_VAR_PROP);
  }

  public void setICAPathVar(String filePathVar) {
    setProperty(FILE_PATH_VAR_PROP, filePathVar);
  }

  @Override
  protected SamplingHandler createHandler() {
    return new SamplingHandler(getName()) {

      @Override
      public String getSamplerData() {
        return "Start Citrix application using " + getICAPathVar();
      }

      @Override
      public void handleSessionEvent(SessionEvent sessionEvent) {
        super.handleSessionEvent(sessionEvent);
        if (sessionEvent.getEventType() == EventType.LOGON) {
          LOGGER.debug("{} on sampler {} detects Citrix session logon",
              Thread.currentThread().getName(), getName());
        }
      }

    };
  }

  @Override
  protected void doClientAction(CitrixClient client)
      throws CitrixClientException, SamplerRunException {

    LOGGER.debug("doClientAction");

    String icaFilePath = getThreadContext().getVariables().get(getICAPathVar());
    try {
      if (icaFilePath == null) {
        throw new IllegalStateException("ICA file path is null for variable:" + getICAPathVar());
      }
      client.setICAFilePath(Paths.get(icaFilePath));

      // Run Citrix client and wait for Logon event
      LOGGER.debug("{} on sampler {} launches a Citrix session", Thread.currentThread().getName(),
          getName());
      client.start(true, FORCE_NORMAL_MODE || GuiPackage.getInstance() != null);

      if (!client.isConnected()) {
        throw new SamplerRunException(ErrorCode.ACTION_LOST_CONNECTION,
            CitrixUtils.getResString("start_application_sampler_not_connected", false));
      }
    } finally {
      if (!KEEP_ICA_FILES && icaFilePath != null && !Paths.get(icaFilePath).toFile().delete()) {
        LOGGER.warn("Cannot delete ica file {}", icaFilePath);
      }
    }
  }

  @Override
  public void threadStarted() {
    LOGGER.info("{} on sampler {} ensures OCR is initialized", Thread.currentThread().getName(),
        getName());
    long start = System.currentTimeMillis();
    OcrManagerHolder.getManager();
    LOGGER.info("OCR initialized in {} millis", System.currentTimeMillis() - start);
  }

  private void resetClient() {
    LOGGER.debug("Reset client");
    CitrixClient client = CitrixSessionHolder.getClient();
    if (client != null) {
      try {
        client.stop(); // Controlled dispose
      } catch (CitrixClientException e) {
        LOGGER.error("{} on sampler {} is unable to stop running Citrix client.",
            Thread.currentThread().getName(), getName(),
            e);
      }
      client = null;
      CitrixSessionHolder.setClient(null);
      LOGGER.debug("Reset client finished");
    }
  }

  @Override
  public void threadFinished() {
    LOGGER.debug("Finishing Thread");
    resetClient();
    LOGGER.debug("Thread Finished");
  }

  /*
   * (non-Javadoc)
   *
   * @see org.apache.jmeter.testelement.TestStateListener#testStarted()
   */
  @Override
  public void testStarted() {
    testStarted("");
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.apache.jmeter.testelement.TestStateListener#testStarted(java.lang.String)
   */
  @Override
  public void testStarted(String host) {
    // NOOP
  }

  /*
   * (non-Javadoc)
   *
   * @see org.apache.jmeter.testelement.TestStateListener#testEnded()
   */
  @Override
  public void testEnded() {
    testEnded(""); //$NON-NLS-1$
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.apache.jmeter.testelement.TestStateListener#testEnded(java.lang.String)
   */
  @Override
  public void testEnded(String host) {
    // NOOP
  }

  @Override
  public void testIterationStart(LoopIterationEvent event) {
    LOGGER.debug("Iteration start");
    resetClient();
  }
}

