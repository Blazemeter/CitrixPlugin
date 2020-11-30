package com.blazemeter.jmeter.citrix.clause.strategy.format;

import com.blazemeter.jmeter.citrix.clause.CheckResult;
import com.blazemeter.jmeter.citrix.clause.Clause;

/**
 * Provides a check result formatter dedicated to displaying the correspondence
 * with the expected value.
 */
public class RegularResultFormatter implements ResultFormatter {

  @Override
  public String execute(CheckResult result, CheckResult previous, Clause clause, int index) {
    String value;
    if (result.isSuccessful()) {
      if (clause.isUsingRegex()) {
        value = "'" + result.getValue() + "' matches the expecting regular expression '"
            + clause.getExpectedValueParametrized() + "'";
      } else {
        value = "'" + result.getValue() + "' is equals to the expecting value '" +
            clause.getExpectedValueParametrized()
            + "'";
      }
    } else {
      if (clause.isUsingRegex()) {
        value = "'" + result.getValue() + "' does not match the expecting regular expression '"
            + clause.getExpectedValueParametrized() + "'";
      } else {
        value = "'" + result.getValue() + "' differs from the expecting value '" +
            clause.getExpectedValueParametrized()
            + "'";
      }
    }
    return value;
  }

}
