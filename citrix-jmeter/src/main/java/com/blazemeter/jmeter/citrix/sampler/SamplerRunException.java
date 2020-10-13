package com.blazemeter.jmeter.citrix.sampler;

/**
 * Provides exception when samplers cannot run.
 */
public class SamplerRunException extends Exception {

  private static final long serialVersionUID = 8096983726542919461L;

  private String code = null;

  /**
   * Instantiate a new {@link SamplerRunException}.
   */
  public SamplerRunException() {
  }

  /**
   * Instantiate a new {@link SamplerRunException}.
   *
   * @param code    the code of the error
   * @param message the description of the error
   */
  public SamplerRunException(ErrorCode code, String message) {
    super(message);
    this.code = code.toString();
  }

  /**
   * Instantiate a new {@link SamplerRunException}.
   *
   * @param code    the code of the error
   * @param message the description of the error
   */
  public SamplerRunException(String code, String message) {
    super(message);
    this.code = code;
  }

  /**
   * Instantiate a new {@link SamplerRunException}.
   *
   * @param code    the code of the error
   * @param message the description of the error
   * @param cause   the cause of this error
   */
  public SamplerRunException(ErrorCode code, String message, Throwable cause) {
    super(message, cause);
    this.code = code.toString();
  }

  /**
   * Instantiate a new {@link SamplerRunException}.
   *
   * @param code    the code of the error
   * @param message the description of the error
   * @param cause   the cause of this error
   */
  public SamplerRunException(String code, String message, Throwable cause) {
    super(message, cause);
    this.code = code;
  }

  /**
   * Instantiate a new {@link SamplerRunException}.
   *
   * @param code               the code of the error
   * @param message            the description of the error
   * @param cause              the cause of this error
   * @param enableSuppression  whether or not suppression is enabled or disabled
   * @param writableStackTrace whether or not the stack trace should be writable
   */
  public SamplerRunException(ErrorCode code, String message, Throwable cause,
                             boolean enableSuppression,
                             boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.code = code.toString();
  }

  /**
   * Instantiate a new {@link SamplerRunException}.
   *
   * @param code               the code of the error
   * @param message            the description of the error
   * @param cause              the cause of this error
   * @param enableSuppression  whether or not suppression is enabled or disabled
   * @param writableStackTrace whether or not the stack trace should be writable
   */
  public SamplerRunException(String code, String message, Throwable cause,
                             boolean enableSuppression,
                             boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.code = code;
  }

  public String code() {
    return code;
  }

  public enum ErrorCode {
    CLAUSE_LOST_CONNECTION,
    CLAUSE_TIMEOUT,
    POLICY_ERROR,
    CLIENT_ERROR,
    ACTION_LOST_CONNECTION
  }
}

