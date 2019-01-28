package com.blazemeter.jmeter.citrix.clause.strategy.check;

import com.blazemeter.jmeter.citrix.clause.CheckResult;
import com.blazemeter.jmeter.citrix.clause.ClauseComputationException;
import com.blazemeter.jmeter.citrix.client.CitrixClient.Snapshot;

/**
 * Represents a method used to check clause using a snapshot while polling
 * Citrix client state
 */
public interface SnapshotChecker {

	/**
	 * Gets the result a clause check from the specified snapshot
	 * 
	 * @param snapshot the snapshot to used to get screen info
	 * @param context  the current polling context
	 * @return the result a clause check from the specified snapshot
	 * @throws ClauseComputationException when clause computation fails
	 */
	CheckResult check(Snapshot snapshot, PollingContext context) throws ClauseComputationException;
}