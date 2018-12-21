package com.blazemeter.jmeter.citrix.client;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blazemeter.jmeter.citrix.client.events.SessionEvent;
import com.blazemeter.jmeter.citrix.client.events.SessionEvent.EventType;
import com.blazemeter.jmeter.citrix.client.events.SessionEvent.KnownError;
import com.blazemeter.jmeter.citrix.client.handler.CitrixClientAdapter;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;

public class SessionErrorLogger extends CitrixClientAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(SessionErrorLogger.class);

	private static KnownError lookupErrorByCode(int code) {
		return Arrays.stream(KnownError.values()).filter(e -> e.getCode() == code).findFirst().orElse(null);
	}

	@Override
	public void handleSessionEvent(SessionEvent sessionEvent) {
		if (sessionEvent != null && sessionEvent.getEventType() == EventType.ERROR) {
			final int code = sessionEvent.getErrorCode();
			KnownError knownError = lookupErrorByCode(code);
			if (knownError == null) {
				LOGGER.error(CitrixUtils.getResString("session_error_logger_unknown_code_fmt", false), code);
			} else {
				switch (knownError) {
				case UNAVAILABLE_SESSION:
					LOGGER.error(CitrixUtils.getResString("session_error_logger_unavailable_session", false));
					break;
				case NO_ERROR:
				default:
					// NOOP
				}
			}
		}
	}
}
