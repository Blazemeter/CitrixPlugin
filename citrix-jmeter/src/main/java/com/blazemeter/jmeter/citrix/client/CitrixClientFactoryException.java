package com.blazemeter.jmeter.citrix.client;

public class CitrixClientFactoryException extends Exception {
  private static final long serialVersionUID = 1L;

  public CitrixClientFactoryException(String message) {
    super(message);
  }

  public CitrixClientFactoryException(String message, Exception e) {
    super(message, e);
  }

}
