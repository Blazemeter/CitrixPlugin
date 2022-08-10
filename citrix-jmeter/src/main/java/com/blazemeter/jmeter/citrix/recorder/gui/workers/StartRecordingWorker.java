package com.blazemeter.jmeter.citrix.recorder.gui.workers;

import com.blazemeter.jmeter.citrix.client.CitrixClientException;
import com.blazemeter.jmeter.citrix.recorder.gui.CitrixRecorderGUI;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartRecordingWorker extends SwingWorker<Boolean, Object> {

  private static final Logger LOGGER = LoggerFactory.getLogger(StartRecordingWorker.class);

  private final Optional<Path> icaPath;
  private final CitrixRecorderGUI crg;

  public StartRecordingWorker(CitrixRecorderGUI crg, Optional<Path> icaPath) {
    this.crg = crg;
    this.icaPath = icaPath;
  }

  /*
   * (non-Javadoc)
   *
   * @see javax.swing.SwingWorker#doInBackground()
   */
  @Override
  protected Boolean doInBackground() throws Exception {
    crg.setWait(true);
    LOGGER.info("Started recording");
    // SwingWorker doesn't allow to define the Thread Name
    // WA, set the thread name inside the running thread
    // Required for set a correct session title and for session instance identification.
    Thread.currentThread().setName("Citrix Recorder 1-1");

    return crg.startRecord(icaPath);

  }

  /*
   * (non-Javadoc)
   *
   * @see javax.swing.SwingWorker#done()
   */
  @Override
  protected void done() {
    crg.setWait(false);
    try {
      Boolean result = get();
      if (Boolean.TRUE.equals(result)) {
        crg.appendStatus("Recording started");
      } else {
        crg.appendStatus(CitrixUtils.getResString(CitrixRecorderGUI.CLIENT_EXCEPTION, false));
        crg.setExpectedDisconnect(true);
        crg.stopRecordingNoCancel();
      }
    } catch (InterruptedException | ExecutionException e) {
      Throwable ex = e.getCause();
      if (ex instanceof CitrixClientException) {
        crg.appendStatus(
            "Recorder Error: " +
                ((CitrixClientException) ex).code() + " " + ex.getMessage());
      } else {
        LOGGER.error("Error occurred starting citrix application {}", e.getMessage(), e);
        crg.appendStatus(CitrixUtils.getResString(CitrixRecorderGUI.CLIENT_EXCEPTION, false));
      }
      crg.setExpectedDisconnect(true);
      crg.stopRecordingNoCancel();
      Thread.currentThread().interrupt();
    }
  }
}

