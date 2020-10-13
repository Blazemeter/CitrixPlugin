package com.blazemeter.jmeter.citrix.client.handler;

/**
 * Provides exception when Citrix client fails.
 */
public class CitrixClientHandlerException extends Exception {

  private static final long serialVersionUID = 8096627550410319461L;

  /**
   * Instantiates a new {@link CitrixClientHandlerException}.
   */
  public CitrixClientHandlerException() {
  }

  /**
   * Instantiates a new {@link CitrixClientHandlerException}.
   *
   * @param message the description of the error
   */
  public CitrixClientHandlerException(String message) {
    super(message);
  }

  /**
   * Instantiates a new {@link CitrixClientHandlerException}.
   *
   * @param cause the cause of this error
   */
  public CitrixClientHandlerException(Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new {@link CitrixClientHandlerException}.
   *
   * @param message the description of the error
   * @param cause   the cause of this error
   */
  public CitrixClientHandlerException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Instantiates a new {@link CitrixClientHandlerException}.
   *
   * @param message            the description of the error
   * @param cause              the cause of this error
   * @param enableSuppression  whether or not suppression is enabled or disabled
   * @param writableStackTrace whether or not the stack trace should be writable
   */
  public CitrixClientHandlerException(String message, Throwable cause, boolean enableSuppression,
                                      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
