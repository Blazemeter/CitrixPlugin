package com.blazemeter.jmeter.citrix.recorder.gui.workers;

import com.blazemeter.jmeter.citrix.recorder.gui.CitrixRecorderGUI;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaitCaptureWorker extends SwingWorker<Boolean, Object> {

  private static final Logger LOGGER = LoggerFactory.getLogger(WaitCaptureWorker.class);

  private final CitrixRecorderGUI crg;

  public WaitCaptureWorker(CitrixRecorderGUI crg) {
    this.crg = crg;
  }

  /*
   * (non-Javadoc)
   *
   * @see javax.swing.SwingWorker#doInBackground()
   */
  @Override
  protected Boolean doInBackground() throws Exception {
    return crg.waitCapture();
  }

  /*
   * (non-Javadoc)
   *
   * @see javax.swing.SwingWorker#done()
   */
  @Override
  protected void done() {
    try {
      boolean eventCaptured = get();
      if (eventCaptured) {
        crg.enableCaptureActions(true);
      }
    } catch (InterruptedException | ExecutionException e) {
      LOGGER.error("Error occurred starting capture interact event {}", e.getMessage(), e);
      Thread.currentThread().interrupt();
    }
  }
}
