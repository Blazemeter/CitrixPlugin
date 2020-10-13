package com.blazemeter.jmeter.citrix.client;

/**
 * Provides exception when Citrix client fails.
 */
public class CitrixClientException extends Exception {

  private static final long serialVersionUID = 8096628374610319461L;

  private String code = null;

  /**
   * Instantiates a new {@link CitrixClientException}.
   */
  public CitrixClientException() {
  }

  /**
   * Instantiates a new {@link CitrixClientException}.
   *
   * @param code    the code of the error
   * @param message the description of the error
   */
  public CitrixClientException(ErrorCode code, String message) {
    super(message);
    this.code = code.toString();
  }

  /**
   * Instantiates a new {@link CitrixClientException}.
   *
   * @param code    the code of the error
   * @param message the description of the error
   */
  public CitrixClientException(String code, String message) {
    super(message);
    this.code = code;
  }

  /**
   * Instantiates a new {@link CitrixClientException}.
   *
   * @param code    the code of the error
   * @param message the description of the error
   * @param cause   the cause of this error
   */
  public CitrixClientException(ErrorCode code, String message, Throwable cause) {
    super(message, cause);
    this.code = code.toString();
  }

  /**
   * Instantiates a new {@link CitrixClientException}.
   *
   * @param code    the code of the error
   * @param message the description of the error
   * @param cause   the cause of this error
   */
  public CitrixClientException(String code, String message, Throwable cause) {
    super(message, cause);
    this.code = code;
  }

  /**
   * Instantiates a new {@link CitrixClientException}.
   *
   * @param code               the code of the error
   * @param message            the description of the error
   * @param cause              the cause of this error
   * @param enableSuppression  whether or not suppression is enabled or disabled
   * @param writableStackTrace whether or not the stack trace should be writable
   */
  public CitrixClientException(ErrorCode code, String message, Throwable cause,
                               boolean enableSuppression,
                               boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.code = code.toString();
  }

  /**
   * Instantiates a new {@link CitrixClientException}.
   *
   * @param code               the code of the error
   * @param message            the description of the error
   * @param cause              the cause of this error
   * @param enableSuppression  whether or not suppression is enabled or disabled
   * @param writableStackTrace whether or not the stack trace should be writable
   */
  public CitrixClientException(String code, String message, Throwable cause,
                               boolean enableSuppression,
                               boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.code = code;
  }

  public String code() {
    return code;
  }

  public enum ErrorCode {
    ICAFILE_TIMEOUT,
    ICAFILE_ERROR,
    CONNECT_TIMEOUT,
    LOGON_TIMEOUT,
    ACTIVEAPP_TIMEOUT,
    START_SESSION_ERROR,
    SCREENSHOT_ERROR,
    KEYBOARD_ERROR,
    MOUSE_ERROR,
  }
}

