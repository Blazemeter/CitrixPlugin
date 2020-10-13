package com.blazemeter.jmeter.citrix.recorder;

import com.blazemeter.jmeter.citrix.listener.CitrixIcaFileSaver;
import java.util.ArrayList;
import java.util.List;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.threads.JMeterVariables;

/**
 * Child of {@link CitrixIcaFileSaver} that exists only to expose
 * the ICA variable to {@link CitrixRecorder}.
 */
public class RecordingCitrixIcaFileSaver extends CitrixIcaFileSaver {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private final List<String> recordedIcaFiles = new ArrayList<>();

  /**
   *
   */
  public RecordingCitrixIcaFileSaver() {
    super();
  }

  /**
   * @param name File name
   */
  RecordingCitrixIcaFileSaver(String name) {
    super(name);
  }

  /**
   * @return the icaFile
   */
  public String getRecordedIcaFile() {
    if (recordedIcaFiles.size() > 1) {
      throw new IllegalStateException("Multiple ICA files were downloaded");
    }
    return recordedIcaFiles.isEmpty() ? null : recordedIcaFiles.get(0);
  }

  /* (non-Javadoc)
   * @see listener.CitrixIcaFileSaver#sampleOccurred(org.apache.jmeter.samplers.SampleEvent)
   */
  @Override
  public void sampleOccurred(SampleEvent e) {
    JMeterVariables variables = getThreadContext().getVariables();
    String fileBefore = variables.get(getVariableName());
    super.sampleOccurred(e);
    String fileAfter = variables.get(getVariableName());
    if (fileBefore == null && fileAfter != null
        || (fileBefore != null && fileAfter != null && !fileBefore.equals(fileAfter))) {
      recordedIcaFiles.add(fileAfter);
    }
  }
}
