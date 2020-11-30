package com.blazemeter.jmeter.citrix.clause;

import com.blazemeter.jmeter.citrix.clause.strategy.check.PollingContext;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a clause to honor to define if a sample succeeds.
 */
// POSSIBLE IMPROVEMENT Do not use final properties
public class Clause implements Serializable {

  public static final Logger LOGGER = LoggerFactory.getLogger(Clause.class);

  private static final long serialVersionUID = 8906973349253054897L;

  private final CheckType checkType;
  private final Rectangle selection;
  private final String expectedValue;
  private final boolean usingRegex;
  private boolean relative;
  // POSSIBLE_IMPROVEMENT Do not store timeout in clause but in sampler
  // This timeout is only used by samplers, assertions and extractors need not it.
  // Consequently, assertions and extractors GUI must hide it.
  private long timeout = ClauseHelper.CLAUSE_TIMEOUT;

  /**
   * Instantiates a new {@link Clause}.
   *
   * @param checkType     the type of check to use
   * @param expectedValue the expected value by a clause check
   */
  public Clause(CheckType checkType, String expectedValue) {
    this(checkType, expectedValue, false, null);
  }

  /**
   * Instantiates a new {@link Clause}.
   *
   * @param checkType     the type of check to use
   * @param expectedValue the expected value by a clause check
   * @param usingRegex    indicates if the expected value is a regular expression
   * @param selection     the area selection to assess on screenshot
   */
  public Clause(CheckType checkType, String expectedValue, boolean usingRegex,
                Rectangle selection) {
    if (checkType == null) {
      throw new IllegalArgumentException("checkType cannot be null.");
    }
    this.checkType = checkType;
    this.selection = selection;
    this.expectedValue = expectedValue;
    this.usingRegex = usingRegex;
  }

  /**
   * Instantiates a new {@link Clause}.
   * <p>
   * Only {@link CheckType check types} that support
   * {@link CheckType#isSupportingSelection() selection}
   *
   * @param checkType  the type of check to use
   * @param screenshot the screenshot of Citrix session
   * @param selection  the area selection to assess on screenshot
   * @param context    the context
   * @throws ClauseComputationException when image assessment fails
   */
  public Clause(CheckType checkType, BufferedImage screenshot, Rectangle selection,
                PollingContext context)
      throws ClauseComputationException {
    this(checkType, checkType.assess(screenshot, selection, context), false, selection);
  }

  /**
   * Gets the type of check used.
   *
   * @return the type of check used
   */
  public final CheckType getCheckType() {
    return checkType;
  }

  /**
   * Gets the area selection to assess on image or session screen.
   *
   * @return the area selection to assess on image or session screen
   */
  public final Rectangle getSelection() {
    return selection;
  }

  /**
   * Gets the expected value for a clause check.
   *
   * @return the expected value for a clause check
   */
  public final String getExpectedValue() {
    return expectedValue;
  }

  public final String getExpectedValueParametrized() {
    return (new CompoundVariable(getExpectedValue())).execute();
  }

  /**
   * Indicates whether the {@link #getSelection() selection} is relative to the
   * foreground window.
   *
   * @return true, if relative; false otherwise
   */
  public final boolean isRelative() {
    return relative;
  }

  /**
   * Defines whether the {@link #getSelection() selection} is relative to the
   * foreground window.
   *
   * @param relative true, if relative; false otherwise
   */
  public final void setRelative(boolean relative) {
    this.relative = relative;
  }

  /**
   * Gets the maximum waiting time used to decide if the clause is honored.
   *
   * @return the maximum waiting time used to decide if the clause is honored
   */
  public final long getTimeout() {
    return timeout;
  }

  /**
   * Defines the maximum waiting time used to decide if the clause is honored.
   *
   * @param timeout the maximum waiting time used to decide if the clause is
   *                honored
   */
  public final void setTimeout(long timeout) {
    this.timeout = timeout;
  }

  /**
   * Indicates whether the {@link #getExpectedValue() expected value} is a regular
   * expression.
   *
   * @return true if the expected value is a regular expression; false otherwise
   */
  public final boolean isUsingRegex() {
    return usingRegex;
  }
}
