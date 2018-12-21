package com.blazemeter.jmeter.citrix.clauses;

/**
 * Provides exception when clause value computation fails.
 */
public class ClauseComputationException extends Exception {

	private static final long serialVersionUID = 8096627550410319461L;

	/**
	 * Instantiate a new {@link ClauseComputationException}
	 */
	public ClauseComputationException() {
	}

	/**
	 * Instantiate a new {@link ClauseComputationException}
	 * 
	 * @param message the description of the error
	 */
	public ClauseComputationException(String message) {
		super(message);
	}

	/**
	 * Instantiate a new {@link ClauseComputationException}
	 * 
	 * @param cause the cause of this error
	 */
	public ClauseComputationException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiate a new {@link ClauseComputationException}
	 * 
	 * @param message the description of the error
	 * @param cause   the cause of this error
	 */
	public ClauseComputationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiate a new {@link ClauseComputationException}
	 * 
	 * @param message            the description of the error
	 * @param cause              the cause of this error
	 * @param enableSuppression  whether or not suppression is enabled or disabled
	 * @param writableStackTrace whether or not the stack trace should be writable
	 */
	public ClauseComputationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
