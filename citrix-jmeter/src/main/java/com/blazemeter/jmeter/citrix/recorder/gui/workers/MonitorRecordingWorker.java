package com.blazemeter.jmeter.citrix.recorder.gui.workers;

import static com.blazemeter.jmeter.citrix.recorder.gui.CitrixRecorderGUI.CLIENT_EXCEPTION;

import com.blazemeter.jmeter.citrix.recorder.gui.CitrixRecorderGUI;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitorRecordingWorker extends SwingWorker<Boolean, Object> {

  private static final Logger LOGGER = LoggerFactory.getLogger(MonitorRecordingWorker.class);

  private final CitrixRecorderGUI crg;

  public MonitorRecordingWorker(CitrixRecorderGUI crg) {
    this.crg = crg;
  }

  @Override
  protected Boolean doInBackground() throws Exception {
    long chunkWaitTime = 1000;
    // Polling over client waiting the finish state
    while (crg.clientIsRunning() && !isCancelled()) {
      Thread.sleep(chunkWaitTime);
      LOGGER.info("[MonitorRecording] Monitoring...");
    }
    return true;
  }

  @Override
  protected void done() {
    try {
      get();
      if (crg.isRecording()) {
        crg.stopRecordingNoCancel();
      }
    } catch (InterruptedException | ExecutionException e) {
      LOGGER.error("Error occurred recording citrix application {}", e.getMessage(), e);
      crg.appendStatus(CitrixUtils.getResString(CLIENT_EXCEPTION, false));
      crg.stopRecordingNoCancel();
      Thread.currentThread().interrupt();
    }
  }
}
