package com.blazemeter.jmeter.citrix.clause.strategy.format;

import com.blazemeter.jmeter.citrix.clause.CheckResult;
import com.blazemeter.jmeter.citrix.clause.Clause;
import com.blazemeter.jmeter.citrix.client.WindowInfo;
import com.blazemeter.jmeter.citrix.client.events.WindowEvent;

/**
 * Provides a check result formatter dedicated to displaying to displaying
 * window events that have occurred.
 */
public class WindowEventResultFormatter implements ResultFormatter {

  private final String expectedState;

  public WindowEventResultFormatter(String expectedState) {
    this.expectedState = expectedState;
  }

  // Format the specified window event
  private static String stringify(WindowEvent event) {
    String value;
    WindowInfo info = event.getWindowInfo();
    if (info != null) {
      value = "'" + event.getWindowState() + "' event occured on window with caption '" +
          info.getCaption() + "'";
    } else {
      value = "'" + event.getWindowState() + "' event occured";
    }
    return value;
  }

  @Override
  public String execute(CheckResult result, CheckResult previous, Clause clause, int index) {
    Object resultValue = result.getValue();
    if (!(resultValue instanceof WindowEvent)) {
      throw new IllegalArgumentException("result must have value property of type WindowEvent.");
    }
    WindowEvent event = (WindowEvent) resultValue;
    String value;
    if (result.isSuccessful()) {
      value = "The expected " + stringify(event);
    } else {
      if (clause.isUsingRegex()) {
        value = stringify(event) + " whereas expecting '" + expectedState
            + "' on window with caption matching regular pattern '" + clause.getExpectedValue() +
            "'";
      } else {
        value =
            stringify(event) + " whereas expecting '" + expectedState + "' on window with caption '"
                + clause.getExpectedValue() + "'";
      }
    }
    return value;
  }

}
