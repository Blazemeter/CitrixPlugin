package com.blazemeter.jmeter.citrix.sampler;

/**
 * Provides exception when samplers cannot run.
 */
public class SamplerRunException extends Exception {

	private static final long serialVersionUID = 8096627550410319461L;

	/**
	 * Instantiate a new {@link SamplerRunException}
	 */
	public SamplerRunException() {
	}

	/**
	 * Instantiate a new {@link SamplerRunException}
	 * 
	 * @param message the description of the error
	 */
	public SamplerRunException(String message) {
		super(message);
	}

	/**
	 * Instantiate a new {@link SamplerRunException}
	 * 
	 * @param cause the cause of this error
	 */
	public SamplerRunException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiate a new {@link SamplerRunException}
	 * 
	 * @param message the description of the error
	 * @param cause   the cause of this error
	 */
	public SamplerRunException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiate a new {@link SamplerRunException}
	 * 
	 * @param message            the description of the error
	 * @param cause              the cause of this error
	 * @param enableSuppression  whether or not suppression is enabled or disabled
	 * @param writableStackTrace whether or not the stack trace should be writable
	 */
	public SamplerRunException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
