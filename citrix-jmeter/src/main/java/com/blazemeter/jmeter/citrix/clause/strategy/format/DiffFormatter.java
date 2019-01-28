package com.blazemeter.jmeter.citrix.clause.strategy.format;

import com.blazemeter.jmeter.citrix.clause.CheckResult;
import com.blazemeter.jmeter.citrix.clause.Clause;

/**
 * Provides a check result formatter dedicated to displaying change detection between two screenshots
 *
 */
public class DiffFormatter implements FormatStrategy {

	@Override
	public String execute(CheckResult result, CheckResult previous, Clause clause, int index) {
		String value;
		if (previous == null) {
			// Dedicated message while no previous result exists
			value = "Unable to identify a change that occurred at the first valid check";
		} else {
			if (result.isSuccessful()) {
				value = "Change detected : '" + result.getValue() + "' differs from previous '" + previous.getValue()
						+ "'";
			} else {
				value = "No change detected : '" + result.getValue() + "' equals to previous '" + previous.getValue()
						+ "'";
			}
		}
		return value;
	}

}
