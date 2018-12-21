package com.blazemeter.jmeter.citrix.clauses;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blazemeter.jmeter.citrix.client.CitrixClient;
import com.blazemeter.jmeter.citrix.client.CitrixClient.Snapshot;
import com.blazemeter.jmeter.citrix.client.CitrixClientException;

public class Clause implements Serializable {

	public static final Logger LOGGER = LoggerFactory.getLogger(Clause.class);

	private static final long serialVersionUID = 8906973349253054897L;

	public enum CheckType {
		HASH, OCR, SESSION_CLOSED;
	}

	public enum SessionState {
		OPEN, CLOSED
	}

	public static final Set<CheckType> SNAPSHOT_CHECKTYPES = EnumSet.of(CheckType.HASH, CheckType.OCR);

	private final CheckType checkType;
	private final Rectangle selection;
	private final String expectedValue;
	private boolean relative;
	private long timeout = ClauseHelper.CLAUSE_TIMEOUT;
	private Set<String> expectedValues;

	public Clause(CheckType checkType, String expectedValue) {
		this(checkType, expectedValue, null);
	}

	public Clause(CheckType checkType, String expectedValue, Rectangle selection) {
		if (checkType == null) {
			throw new IllegalArgumentException("checkType cannot be null.");
		}
		this.checkType = checkType;

		if (checkType == CheckType.SESSION_CLOSED) {
			this.selection = null;
			this.expectedValue = SessionState.CLOSED.name();
		} else {
			this.selection = selection;
			this.expectedValue = expectedValue;

			if (expectedValue == null || expectedValue.trim().length() == 0) {
				expectedValues = new HashSet<>(0);
			} else if (expectedValue.indexOf('|') >= 0) {
				String[] values = expectedValue.split("\\|");
				expectedValues = new HashSet<>(values.length);
				for (int i = 0; i < values.length; i++) {
					String value = values[i].trim();
					if (value.length() > 0) {
						expectedValues.add(value);
					}
				}
			} else {
				expectedValues = new HashSet<>(1);
				expectedValues.add(expectedValue.trim());
			}
		}
	}

	public Clause(CheckType checkType, BufferedImage screenshot, Rectangle selection)
			throws ClauseComputationException {
		this(checkType, ClauseHelper.computeValue(checkType, screenshot, selection), selection);
	}

	public final CheckType getCheckType() {
		return checkType;
	}

	public final Rectangle getSelection() {
		return selection;
	}

	public final String getExpectedValue() {
		return expectedValue;
	}

	public final boolean isRelative() {
		return relative;
	}

	public final void setRelative(boolean relative) {
		this.relative = relative;
	}

	public final long getTimeout() {
		return timeout;
	}

	public final void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public CheckResult check(CitrixClient client) throws ClauseComputationException, CitrixClientException {
		if (client == null) {
			throw new IllegalArgumentException("client must not be null.");
		}

		CheckResult result;
		if (CheckType.SESSION_CLOSED == getCheckType()) {
			boolean connected = client.isConnected();
			SessionState state = connected ? SessionState.OPEN : SessionState.CLOSED;
			result = new CheckResult(null, state.name(), !connected);
		} else {
			result = check(client.takeSnapshot());
		}
		return result;
	}

	public CheckResult check(Snapshot snapshot) throws ClauseComputationException {
		if (!SNAPSHOT_CHECKTYPES.contains(getCheckType())) {
			throw new IllegalStateException(MessageFormat.format("{0} clause cannot check snapshot.", getCheckType()));
		}
		if (snapshot == null) {
			throw new IllegalArgumentException("snapshot must not be null.");
		}
		
		// Transform selection property into selection with absolute position
		Rectangle absoluteSelection = ClauseHelper.getAbsoluteSelection(selection, isRelative(), snapshot.getForegroundWindowArea());
		
		// Do computation value on the absolute area
		String computedValue = ClauseHelper.computeValue(checkType, snapshot.getScreenshot(), absoluteSelection);
		
		return new CheckResult(snapshot, computedValue, expectedValues.contains(computedValue));
	}

	/**
	 * Provides information on the clauses check
	 */
	public static class CheckResult {
		private final String computedValue;
		private final Snapshot snapshot;
		private final boolean successful;

		/**
		 * Gets the computed value during this check.
		 * 
		 * @return the computed value during this check
		 */
		public String getComputedValue() {
			return computedValue;
		}

		/**
		 * Gets the snapshot used during this check.
		 * 
		 * @return the snapshot used during this check
		 */
		public Snapshot getSnapshot() {
			return snapshot;
		}

		/**
		 * Indicates whether the check succeeded
		 * 
		 * @return true, if the check succeeded; false otherwise
		 */
		public boolean isSuccessful() {
			return successful;
		}

		/**
		 * Instantiates a new {@link CheckResult}
		 * 
		 * @param snapshot      the snapshot used during this check
		 * @param computedValue the computed value during this check
		 * @param successful    true, if the check succeeded; false otherwise
		 */
		public CheckResult(Snapshot snapshot, String computedValue, boolean successful) {
			this.snapshot = snapshot;
			this.computedValue = computedValue;
			this.successful = successful;
		}

	}
}
