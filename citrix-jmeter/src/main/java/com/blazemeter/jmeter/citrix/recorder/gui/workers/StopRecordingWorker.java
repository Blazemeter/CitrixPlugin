package com.blazemeter.jmeter.citrix.recorder.gui.workers;

import com.blazemeter.jmeter.citrix.client.CitrixClientException;
import com.blazemeter.jmeter.citrix.recorder.gui.CitrixRecorderGUI;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import com.blazemeter.jmeter.citrix.utils.DialogHelper;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StopRecordingWorker extends SwingWorker<Boolean, Object> {

  private static final Logger LOGGER = LoggerFactory.getLogger(StopRecordingWorker.class);

  private final CitrixRecorderGUI crg;

  public StopRecordingWorker(CitrixRecorderGUI crg) {
    this.crg = crg;
  }

  @Override
  protected Boolean doInBackground() throws Exception {
    return crg.stopRecordingUI();
  }

  @Override
  protected void done() {
    crg.setWait(false);
    try {
      get();
      crg.stopRecordingNoCancel();
    } catch (InterruptedException | ExecutionException e) {
      Throwable ex = e.getCause();
      if (ex instanceof CitrixClientException) {
        crg.appendStatus(
            "Unable to stop Recorder: " +
                ((CitrixClientException) ex).code() + " " + ex.getMessage());
      } else {
        LOGGER.error("Error occurred stopping citrix recorder {}", e.getMessage(), e);
        crg.appendStatus(CitrixUtils.getResString(CitrixRecorderGUI.CLIENT_EXCEPTION, false));
        crg.stopRecordingNoCancel();
        Thread.currentThread().interrupt();
      }
    } finally {
      DialogHelper.focusJMeter();
    }

  }
}
