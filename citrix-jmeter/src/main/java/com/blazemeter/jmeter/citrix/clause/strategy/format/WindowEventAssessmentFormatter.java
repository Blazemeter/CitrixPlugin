package com.blazemeter.jmeter.citrix.clause.strategy.format;

import com.blazemeter.jmeter.citrix.client.WindowInfo;
import com.blazemeter.jmeter.citrix.client.events.WindowEvent;

public class WindowEventAssessmentFormatter implements AssessmentFormatter {

  @Override
  public String execute(Object value) {
    String result = null;
    if (value instanceof WindowEvent) {
      WindowEvent event = (WindowEvent) value;
      WindowInfo info = event.getWindowInfo();
      result = info != null ? info.getCaption() : null;
    }
    return result;
  }

}
