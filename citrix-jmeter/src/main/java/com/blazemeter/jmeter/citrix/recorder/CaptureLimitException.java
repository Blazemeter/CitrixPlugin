package com.blazemeter.jmeter.citrix.recorder;

/**
 * Provides exception when capture size exceeds.
 */
public class CaptureLimitException extends Exception {

  private static final long serialVersionUID = 8096627550410319461L;

  private final int size;

  /**
   * Instantiate a new {@link CaptureLimitException}.
   *
   * @param size               current size of the capture
   * @param message            the description of the error
   * @param cause              the cause of this error
   * @param enableSuppression  whether or not suppression is enabled or disabled
   * @param writableStackTrace whether or not the stack trace should be writable
   */
  public CaptureLimitException(int size, String message, Throwable cause, boolean enableSuppression,
                               boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.size = size;
  }

  /**
   * Instantiate a new {@link CaptureLimitException}.
   *
   * @param size    current size of the capture
   * @param message the description of the error
   * @param cause   the cause of this error
   */
  public CaptureLimitException(int size, String message, Throwable cause) {
    super(message, cause);
    this.size = size;
  }

  /**
   * Instantiate a new {@link CaptureLimitException}.
   *
   * @param size  current size of the capture
   * @param cause the cause of this error
   */
  public CaptureLimitException(int size, Throwable cause) {
    super(cause);
    this.size = size;
  }

  /**
   * Instantiate a new {@link CaptureLimitException}.
   *
   * @param size    current size of the capture
   * @param message the description of the error
   */
  public CaptureLimitException(int size, String message) {
    super(message);
    this.size = size;
  }

  /**
   * Instantiate a new {@link CaptureLimitException}.
   *
   * @param size current size of the capture
   */
  public CaptureLimitException(int size) {
    this.size = size;
  }

  public int getSize() {
    return size;
  }

}
