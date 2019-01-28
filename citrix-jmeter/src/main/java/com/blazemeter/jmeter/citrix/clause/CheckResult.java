package com.blazemeter.jmeter.citrix.clause;

import com.blazemeter.jmeter.citrix.client.CitrixClient.Snapshot;

/**
 * Provides information on clause check
 */
public class CheckResult {
	private final Object value;
	private final Snapshot snapshot;
	private final boolean successful;

	/**
	 * Gets the computed value during this check.
	 * 
	 * @return the computed value during this check
	 */
	public Object getValue() {
		return value;
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
	 * @param snapshot   the snapshot used during this check
	 * @param value      the computed value during this check
	 * @param successful true, if the check succeeded; false otherwise
	 */
	public CheckResult(Snapshot snapshot, Object value, boolean successful) {
		this.snapshot = snapshot;
		this.value = value;
		this.successful = successful;
	}

}