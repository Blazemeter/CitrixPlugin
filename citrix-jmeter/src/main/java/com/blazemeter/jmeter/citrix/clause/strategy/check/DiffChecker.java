package com.blazemeter.jmeter.citrix.clause.strategy.check;

import java.util.Objects;

import com.blazemeter.jmeter.citrix.clause.CheckResult;

/**
 * Provides a {@link ClientChecker} that can detect difference on Citrix session
 * screen
 */
public class DiffChecker extends AbstractScreenChecker {

	/**
	 * Instantiates a new {@link DiffChecker}
	 * 
	 * @param imageAssessor the function called to do screenshot assessment
	 */
	public DiffChecker(ScreenshotAssessor imageAssessor) {
		super(imageAssessor);
	}

	@Override
	protected boolean isSuccess(PollingContext context, String resultValue) {
		// Compare with the previous result if it exists
		CheckResult previous = context.getPrevious();
		return previous != null && !Objects.equals(resultValue, previous.getValue());
	}

}