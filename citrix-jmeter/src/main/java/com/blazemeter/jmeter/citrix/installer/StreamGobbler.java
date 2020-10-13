package com.blazemeter.jmeter.citrix.installer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Gobbles output of Stderr / Stdout when using Runtime.exec().
 */
public class StreamGobbler extends Thread {
  private final InputStream is;
  private String result;
  private String exceptionResult;

  public StreamGobbler(InputStream is) {
    this.is = is;
  }

  /**
   * @see java.lang.Thread#run()
   */
  public void run() {
    StringBuilder buffer = new StringBuilder();
    try (InputStreamReader isr = new InputStreamReader(is);
         BufferedReader br = new BufferedReader(isr)) {
      String line;
      while ((line = br.readLine()) != null) {
        buffer.append(line).append("\n");
      }
      result = buffer.toString();
    } catch (IOException ioe) {
      exceptionResult = ExceptionUtils.getStackTrace(ioe);
    }
  }

  /**
   * @return String Result
   */
  public String getResult() {
    return result + (exceptionResult == null ? "" : ", exception:" + exceptionResult);
  }
}
