package com.blazemeter.jmeter.citrix.client.events;

import com.blazemeter.jmeter.citrix.client.CitrixClient;

/**
 * Provides a Citrix session event
 *
 */
public class SessionEvent extends ClientEvent {

	private static final long serialVersionUID = 2386977824636875968L;

	/**
	 * Enumerates the different types of session event
	 * 
	 */
	public enum EventType {
		/**
		 * ICA session is connected
		 */
		CONNECT,

		/**
		 * ICA session is disconnected
		 */
		DISCONNECT,

		/**
		 * An error has occurred
		 */
		ERROR,

		/**
		 * Citrix client is visible
		 */
		SHOW,

		/**
		 * Citrix client is hidden
		 */
		HIDE,

		/**
		 * User is logged
		 */
		LOGON,

		/**
		 * User is logged out
		 */
		LOGOFF;
	}

	public enum KnownError {
		/**
		 * No error detected
		 */
		NO_ERROR(0),

		/**
		 * The Citrix session is not available due to misconfiguration in Windows
		 * regitry (Windows only).
		 */
		UNAVAILABLE_SESSION(412);

		private final int code;

		public int getCode() {
			return code;
		}

		private KnownError(int code) {
			this.code = code;
		}
	}

	private final EventType eventType;
	private final int errorCode;

	/**
	 * Gets the type of this event
	 * 
	 * @return the type of this event
	 */
	public EventType getEventType() {
		return eventType;
	}

	public int getErrorCode() {
		return errorCode;
	}

	/**
	 * Instantiates a new {@link SessionEvent}
	 * 
	 * @param source    the source of the session event
	 * @param eventType the type of event
	 * @param errorCode code used to identify error when eventType is
	 *                  {@link EventType#ERROR}
	 * @throws IllegalArgumentException when eventType is null or eventType is is
	 *                                  {@link EventType#ERROR} and errorCode is
	 *                                  {@link KnownError#NO_ERROR}
	 */
	public SessionEvent(CitrixClient source, EventType eventType, int errorCode) {
		super(source);
		if (eventType == null) {
			throw new IllegalArgumentException("eventType cannot be null.");
		}
		if (eventType == EventType.ERROR && errorCode == KnownError.NO_ERROR.getCode()) {
			throw new IllegalArgumentException(
					"Error event type requires an error code different from " + KnownError.NO_ERROR.getCode());
		}
		this.eventType = eventType;
		this.errorCode = eventType == EventType.ERROR ? errorCode : KnownError.NO_ERROR.getCode();

	}

	/**
	 * <p>
	 * Instantiates a new {@link SessionEvent}
	 * </p>
	 * 
	 * <p>
	 * Use {@link SessionEvent(CitrixClient, EventType, int)} to instantiate
	 * error events.
	 * </p>
	 * 
	 * @param source    the source of the session event
	 * @param eventType the type of event
	 * @throws IllegalArgumentException See {@link SessionEvent(CitrixClient,
	 *                                  EventType, int)}
	 */
	public SessionEvent(CitrixClient source, EventType eventType) {
		this(source, eventType, KnownError.NO_ERROR.getCode());
	}
}