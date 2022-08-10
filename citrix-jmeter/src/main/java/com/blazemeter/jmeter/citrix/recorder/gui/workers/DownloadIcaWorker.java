package com.blazemeter.jmeter.citrix.recorder.gui.workers;

import com.blazemeter.jmeter.citrix.recorder.gui.CitrixRecorderGUI;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadIcaWorker extends SwingWorker<Optional<Path>, Object> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadIcaWorker.class);

  private final CitrixRecorderGUI crg;

  public DownloadIcaWorker(CitrixRecorderGUI crg) {
    this.crg = crg;
  }

  @Override
  protected Optional<Path> doInBackground() throws Exception {
    return crg.downloadIcaFile();
  }

  /*
   * (non-Javadoc)
   *
   * @see javax.swing.SwingWorker#done()
   */
  @Override
  protected void done() {
    crg.setWait(false);
    Optional<Path> path;
    try {
      path = get();
      crg.downloadIcaFileDone(path);
    } catch (InterruptedException | ExecutionException e) {
      LOGGER.error("ICA file downloading error : {}", e.getMessage(), e);
      crg.appendStatus(CitrixUtils.getResString("recorder_ica_downloading_failed", false));
      crg.stopRecordingNoCancel();
      Thread.currentThread().interrupt();
    }
  }
}
