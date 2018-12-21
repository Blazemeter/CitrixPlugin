package com.blazemeter.jmeter.citrix.client;

/**
 * Provides exception when Citrix client fails.
 */
public class CitrixClientException extends Exception {

	private static final long serialVersionUID = 8096627550410319461L;

	/**
	 * Instantiates a new {@link CitrixClientException}
	 */
	public CitrixClientException() {
	}

	/**
	 * Instantiates a new {@link CitrixClientException}
	 * 
	 * @param message the description of the error
	 */
	public CitrixClientException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new {@link CitrixClientException}
	 * 
	 * @param cause the cause of this error
	 */
	public CitrixClientException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new {@link CitrixClientException}
	 * 
	 * @param message the description of the error
	 * @param cause   the cause of this error
	 */
	public CitrixClientException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new {@link CitrixClientException}
	 * 
	 * @param message            the description of the error
	 * @param cause              the cause of this error
	 * @param enableSuppression  whether or not suppression is enabled or disabled
	 * @param writableStackTrace whether or not the stack trace should be writable
	 */
	public CitrixClientException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
