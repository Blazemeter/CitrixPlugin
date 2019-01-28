package com.blazemeter.jmeter.citrix.clause.strategy.check;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.blazemeter.jmeter.citrix.clause.CheckResult;
import com.blazemeter.jmeter.citrix.clause.ClauseComputationException;
import com.blazemeter.jmeter.citrix.client.CitrixClient;
import com.blazemeter.jmeter.citrix.client.CitrixClientException;
import com.blazemeter.jmeter.citrix.client.CitrixClient.Snapshot;

/**
 * Provides an abstract base class for {@link ClientChecker} that use screenshot
 * of Citrix session
 */
public abstract class AbstractScreenChecker implements ClientChecker, ScreenshotAssessor {

	private final ScreenshotAssessor screenshotAssessor;

	/**
	 * Instantiates a new {@link AbstractScreenChecker}
	 * 
	 * @param screenshotAssessor the function called to do screenshot assessment
	 */
	protected AbstractScreenChecker(ScreenshotAssessor screenshotAssessor) {
		if (screenshotAssessor == null) {
			throw new IllegalArgumentException("screenshotAssessor must not be null.");
		}
		this.screenshotAssessor = screenshotAssessor;
	}

	/**
	 * Gets the result a clause check from the specified snapshot
	 * 
	 * @param snapshot the snapshot to used to get screen info
	 * @param context  the current polling context
	 * @return the result a clause check from the specified snapshot
	 * @throws ClauseComputationException when clause computation fails
	 */
	protected CheckResult checkSnapshot(Snapshot snapshot, PollingContext context) throws ClauseComputationException {
		// Get real area selection depending to foreground info
		Rectangle selection = context.getAreaSelector().select(snapshot.getForegroundWindowArea());

		// Do screenshot assessment
		String value = assess(snapshot.getScreenshot(), selection);

		return new CheckResult(snapshot, value, isSuccess(context, value));
	}

	/**
	 * Indicates whether the specified result value is right taking into account the
	 * polling context
	 * 
	 * @param context     the polling context
	 * @param resultValue the value to test
	 * @return true, if the specified result value is right taking into account the
	 *         polling context; false otherwise
	 */
	protected abstract boolean isSuccess(PollingContext context, String resultValue);

	@Override
	public final CheckResult check(CitrixClient client, PollingContext context)
			throws CitrixClientException, ClauseComputationException {
		return checkSnapshot(client.takeSnapshot(), context);
	}

	@Override
	public String assess(BufferedImage image, Rectangle selection) throws ClauseComputationException {
		return screenshotAssessor.assess(image, selection);
	}

}