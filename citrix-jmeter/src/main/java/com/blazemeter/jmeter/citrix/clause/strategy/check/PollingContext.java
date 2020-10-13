package com.blazemeter.jmeter.citrix.clause.strategy.check;

import com.blazemeter.jmeter.citrix.clause.CheckResult;
import com.blazemeter.jmeter.citrix.clause.Clause;
import java.awt.Rectangle;
import java.util.function.Predicate;

/**
 * Provides a context shared by each clause checking while polling.
 * <p>
 * This class allows to clause checks to be compliant with the current clause
 * definition.
 */
public class PollingContext {
  private final Predicate<String> valuePredicate;
  private final AreaSelector areaSelector;
  private final Clause clause;
  private CheckResult previous = null;

  /**
   * Instantiates a new {@link PollingContext}.
   *
   * @param valuePredicate the predicate used to evaluate the clause check result
   * @param areaSelector   the screenshot area selector
   * @param clause         the Clause
   */
  public PollingContext(Predicate<String> valuePredicate, AreaSelector areaSelector,
                        Clause clause) {
    this.valuePredicate = valuePredicate;
    this.areaSelector = areaSelector;
    this.clause = clause;
  }

  /**
   * Gets the predicate used to evaluate the clause check result.
   * <p>
   * Can be null if the current checking strategy does not use
   * {@link Clause#getExpectedValue() expected value}
   *
   * @return the predicate used to evaluate the clause check result
   */
  public Predicate<String> getValuePredicate() {
    return valuePredicate;
  }

  /**
   * Gets the screenshot area selector.
   *
   * @return the screenshot area selector
   */
  public AreaSelector getAreaSelector() {
    return areaSelector;
  }

  /**
   * Gets the previous check result.
   *
   * @return the previous check result
   */
  public CheckResult getPrevious() {
    return previous;
  }

  /**
   * Sets the previous check result.
   *
   * @param previous the previous check result to set
   */
  public void setPrevious(CheckResult previous) {
    this.previous = previous;
  }

  public Clause getClause() {
    return this.clause;
  }

  /**
   * Represents a screenshot area selector.
   */
  public interface AreaSelector {
    /**
     * Gets selection based on the specified foreground area.
     *
     * @param fgArea the foreground area
     * @return the area selection
     */
    Rectangle select(Rectangle fgArea);
  }
}
