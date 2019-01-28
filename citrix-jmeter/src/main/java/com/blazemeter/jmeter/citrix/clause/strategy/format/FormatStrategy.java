package com.blazemeter.jmeter.citrix.clause.strategy.format;

import com.blazemeter.jmeter.citrix.clause.CheckResult;
import com.blazemeter.jmeter.citrix.clause.Clause;

/**
 * Represents a method used to format a clause check result
 */
public interface FormatStrategy {

	/**
	 * Run the method to format a clause check result
	 * 
	 * @param result   the result to format
	 * @param previous the previous result
	 * @param clause   the checked clause
	 * @param index    the rank of the check
	 * @return A formatted message describing the check result
	 */
	String execute(CheckResult result, CheckResult previous, Clause clause, int index);
}
