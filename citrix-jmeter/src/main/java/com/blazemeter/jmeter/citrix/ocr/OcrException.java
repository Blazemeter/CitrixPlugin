package com.blazemeter.jmeter.citrix.ocr;

/**
 * OCR Related exception.
 */
public class OcrException extends Exception {

  private static final long serialVersionUID = 706759572008574062L;

  /**
   * Instantiates a new {@link OcrException}.
   */
  public OcrException() {
    super();
  }

  /**
   * Instantiates a new {@link OcrException}.
   *
   * @param message the description of the error
   */
  public OcrException(String message) {
    super(message);
  }

  /**
   * Instantiates a new {@link OcrException}.
   *
   * @param cause the cause of this error
   */
  public OcrException(Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new {@link OcrException}.
   *
   * @param message the description of the error
   * @param cause   the cause of this error
   */
  public OcrException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Instantiates a new {@link OcrException}.
   *
   * @param message            the description of the error
   * @param cause              the cause of this error
   * @param enableSuppression  whether or not suppression is enabled or disabled
   * @param writableStackTrace whether or not the stack trace should be writable
   */
  public OcrException(String message, Throwable cause, boolean enableSuppression,
                      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
