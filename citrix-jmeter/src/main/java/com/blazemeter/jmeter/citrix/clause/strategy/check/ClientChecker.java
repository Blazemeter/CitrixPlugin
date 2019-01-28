package com.blazemeter.jmeter.citrix.clause.strategy.check;

import com.blazemeter.jmeter.citrix.clause.CheckResult;
import com.blazemeter.jmeter.citrix.clause.ClauseComputationException;
import com.blazemeter.jmeter.citrix.client.CitrixClient;
import com.blazemeter.jmeter.citrix.client.CitrixClientException;

/**
 * Represents a method used to check clause while Citrix client polling
 */
public interface ClientChecker {

	/**
	 * Gets a check result based on the specified polling context
	 * 
	 * @param client  the Citrix client to use
	 * @param context the polling context
	 * @return the result of the clause check
	 * @throws CitrixClientException      when Citrix client fails to operate
	 * @throws ClauseComputationException when clause computation fails
	 */
	CheckResult check(CitrixClient client, PollingContext context)
			throws CitrixClientException, ClauseComputationException;
}