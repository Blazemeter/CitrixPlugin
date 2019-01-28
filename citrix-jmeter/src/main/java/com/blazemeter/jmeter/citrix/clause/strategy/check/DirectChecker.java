package com.blazemeter.jmeter.citrix.clause.strategy.check;

import com.blazemeter.jmeter.citrix.clause.CheckResult;
import com.blazemeter.jmeter.citrix.clause.ClauseComputationException;
import com.blazemeter.jmeter.citrix.client.CitrixClient.Snapshot;

/**
 * Provides a {@link ClientChecker} that can do assessment on Citrix session
 * screen
 */
public class DirectChecker extends AbstractScreenChecker implements SnapshotChecker {

	/**
	 * Instantiates a new {@link DirectChecker}
	 * 
	 * @param imageAssessor the function called to do screenshot assessment
	 */
	public DirectChecker(ScreenshotAssessor imageAssessor) {
		super(imageAssessor);
	}

	@Override
	protected boolean isSuccess(PollingContext context, String resultValue) {
		// Using value predicate from the polling context
		return context.getValuePredicate().test(resultValue);
	}

	@Override
	public CheckResult check(Snapshot snapshot, PollingContext context) throws ClauseComputationException {
		return checkSnapshot(snapshot, context);
	}
}