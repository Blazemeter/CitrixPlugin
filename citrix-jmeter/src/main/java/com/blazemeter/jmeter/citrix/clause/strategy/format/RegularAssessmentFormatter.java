package com.blazemeter.jmeter.citrix.clause.strategy.format;

public class RegularAssessmentFormatter implements AssessmentFormatter {

  @Override
  public String execute(Object value) {
    return value != null ? value.toString() : null;
  }

}
