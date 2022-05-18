package com.blazemeter.jmeter.citrix.clause;

import com.blazemeter.jmeter.citrix.assertion.CitrixAssertion;
import com.blazemeter.jmeter.citrix.clause.strategy.check.CheckStrategy;
import com.blazemeter.jmeter.citrix.clause.strategy.check.CheckStrategy.CheckResultCallback;
import com.blazemeter.jmeter.citrix.clause.strategy.check.DiffChecker;
import com.blazemeter.jmeter.citrix.clause.strategy.check.DirectChecker;
import com.blazemeter.jmeter.citrix.clause.strategy.check.ListeningStrategy;
import com.blazemeter.jmeter.citrix.clause.strategy.check.PollingContext;
import com.blazemeter.jmeter.citrix.clause.strategy.check.PollingStrategy;
import com.blazemeter.jmeter.citrix.clause.strategy.format.AssessmentFormatter;
import com.blazemeter.jmeter.citrix.clause.strategy.format.DiffResultFormatter;
import com.blazemeter.jmeter.citrix.clause.strategy.format.RegularAssessmentFormatter;
import com.blazemeter.jmeter.citrix.clause.strategy.format.RegularResultFormatter;
import com.blazemeter.jmeter.citrix.clause.strategy.format.ResultFormatter;
import com.blazemeter.jmeter.citrix.clause.strategy.format.WindowEventAssessmentFormatter;
import com.blazemeter.jmeter.citrix.clause.strategy.format.WindowEventResultFormatter;
import com.blazemeter.jmeter.citrix.client.CitrixClient;
import com.blazemeter.jmeter.citrix.client.CitrixClient.Snapshot;
import com.blazemeter.jmeter.citrix.client.WindowInfo;
import com.blazemeter.jmeter.citrix.client.events.WindowEvent.WindowState;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Enumerates the types of check used to ensure a {@link Clause} is honored.
 */
public enum CheckType {

  /**
   * This check uses hash computation on image or screen of the Citrix session to
   * get expected result.
   */
  HASH(new PollingStrategy(true, new DirectChecker(ClauseHelper::hash)),
      new RegularResultFormatter()),

  /**
   * This check uses text recognition on image/screen of the Citrix session to get
   * expected result.
   */
  OCR(new PollingStrategy(true, new DirectChecker(ClauseHelper::recognize)),
      new RegularResultFormatter()),

  /**
   * This check detects changes on image or screen of the Citrix session using
   * hash computation.
   */
  HASH_CHANGED(new PollingStrategy(false, new DiffChecker(ClauseHelper::hash)),
      new DiffResultFormatter()),

  /**
   * This check detects changes on image or screen of the Citrix session using
   * text recognition.
   */
  OCR_CHANGED(new PollingStrategy(false, new DiffChecker(ClauseHelper::recognize)),
      new DiffResultFormatter()),

  /**
   * This check ensures the Citrix session is closed.
   */
  SESSION_CLOSED(new PollingStrategy(false, (c, p) -> {
    boolean connected = c.isConnected();
    SessionState state = connected ? SessionState.OPEN : SessionState.CLOSED;
    return new CheckResult(null, state, !connected);
  }), (r, p, c, i) -> {
    String value;
    if (r.isSuccessful()) {
      value = "The Citrix session is closed.";
    } else {
      value = "The Citrix session is still open when it is expected to be closed.";
    }
    return value;
  }),

  /**
   * This check ensures a window with compliant caption exists in Citrix session.
   */
  WINDOW_EXISTS(new PollingStrategy(true, (c, p) -> {
    Optional<String> optionalCaption = c.getWindowInfos().stream().map(WindowInfo::getCaption)
        .filter(p.getValuePredicate()).findFirst();
    return new CheckResult(null, optionalCaption.orElse(""), optionalCaption.isPresent());
  }), (r, p, c, i) -> {
    String value;
    if (r.isSuccessful()) {
      if (c.isUsingRegex()) {
        value = "Found window with caption '" + r.getValue() +
            "' matching the expected regular expression '"
            + c.getExpectedValue() + "'";
      } else {
        value = "Found window with caption '" + r.getValue() + "' matching the expected value '"
            + c.getExpectedValue() + "'";
      }
    } else {
      if (c.isUsingRegex()) {
        value =
            "Window with caption matching the expected regular expression '" + c.getExpectedValue()
                + "' not found";
      } else {
        value = "Window with caption matching the expected value '" + c.getExpectedValue() +
            "' not found";
      }
    }
    return value;
  }),

  /**
   * This check ensures a window with compliant caption is closed during waiting.
   */
  WINDOW_CLOSED(new ListeningStrategy(e -> e.getWindowState() == WindowState.CLOSED),
      new WindowEventResultFormatter(WindowState.CLOSED.name()),
      new WindowEventAssessmentFormatter()),

  /**
   * This check ensures a window with compliant caption gets the foreground during
   * waiting.
   */
  WINDOW_FOREGROUND(new ListeningStrategy(e -> e.getWindowState() == WindowState.FOREGROUND),
      new WindowEventResultFormatter(WindowState.FOREGROUND.name()),
      new WindowEventAssessmentFormatter());

  /**
   * Gets the check types that can be used by {@link CitrixAssertion}.
   */
  public static final Set<CheckType> ASSERTION_CHECKS = Arrays.stream(CheckType.values()) //NOSONAR
      .filter(c -> c.checkStrategy.isSupportingImageAssessment() &&
          c.checkStrategy.isSupportingSnapshot())
      .collect(Collectors.toSet());

  private final CheckStrategy checkStrategy;
  private final ResultFormatter resultFormatter;
  private final AssessmentFormatter assessmentFormatter;

  /**
   * Instantiates a new {@link CheckType}.
   *
   * @param checkStrategy       the strategy used to check a clause
   * @param resultFormatter     the strategy used to format check result
   * @param assessmentFormatter the strategy used to format assessment from check
   *                            result
   */
  CheckType(CheckStrategy checkStrategy, ResultFormatter resultFormatter,
            AssessmentFormatter assessmentFormatter) {
    if (checkStrategy == null) {
      throw new IllegalArgumentException("checkStrategy must not be null.");
    }
    if (resultFormatter == null) {
      throw new IllegalArgumentException("resultFormatter,  must not be null.");
    }
    if (assessmentFormatter == null) {
      throw new IllegalArgumentException("assessmentFormatter  must not be null.");
    }
    this.checkStrategy = checkStrategy;
    this.resultFormatter = resultFormatter;
    this.assessmentFormatter = assessmentFormatter;
  }

  /**
   * Instantiates a new {@link CheckType}.
   *
   * @param checkStrategy        the strategy used to check a clause
   * @param resultFormatStrategy the strategy used to format check result
   */
  CheckType(CheckStrategy checkStrategy, ResultFormatter resultFormatStrategy) {
    this(checkStrategy, resultFormatStrategy, new RegularAssessmentFormatter());
  }

  /**
   * Indicates whether this check can use area selection on image.
   *
   * @return true, if this check can use area selection on image; false otherwise
   */
  public boolean isSupportingSelection() {
    return checkStrategy.isSupportingImageAssessment();
  }

  /**
   * Indicates whether this check uses {@link Clause#getExpectedValue()} to get a
   * result.
   *
   * @return true if this check uses {@link Clause#getExpectedValue()} to get a
   * result;false otherwise
   */
  public boolean isUsingExpectedValue() {
    return checkStrategy.isUsingExpectedValue();
  }

  /**
   * Waits a clause is honored using the specified Citrix client.
   *
   * @param clause  the clause to be expected
   * @param client  the Citrix client used to compute clause result
   * @param onCheck the callback method used when a check occurs during waiting
   * @return true, if the clause is honored before the timeout specified by clause
   * @throws InterruptedException when waiting is interrupted
   */
  public boolean wait(Clause clause, CitrixClient client, CheckResultCallback onCheck)
      throws InterruptedException {
    return checkStrategy.wait(clause, client, onCheck);
  }

  /**
   * Checks a clause is honored using the specified snapshot.
   * <p>
   * This method does not wait the clause is honored. Instead it computes directly
   * a clause result value from the snapshot and provides this result.
   *
   * @param clause   the clause to test
   * @param snapshot the snapshot to test
   * @return the check result
   * @throws ClauseComputationException when clause value computation fails
   */
  public CheckResult checkSnapshot(Clause clause, Snapshot snapshot)
      throws ClauseComputationException {
    return checkStrategy.checkSnapshot(clause, snapshot);
  }

  /**
   * Gets assessment from the specified image.
   *
   * @param image     the image to assess
   * @param selection the sub area of the image used to get assessment; whole
   *                  image if null
   * @param context   the context
   * @return the assessment
   * @throws ClauseComputationException when assessment computation fails
   */
  public String assess(BufferedImage image, Rectangle selection, PollingContext context)
      throws ClauseComputationException {
    return checkStrategy.assess(image, selection, context);
  }

  /**
   * Format the specified clause check result.
   *
   * @param result   the result to format
   * @param previous the previous result
   * @param clause   the checked clause
   * @param index    the rank of the check
   * @return A formatted message describing the check result
   */
  public String formatResult(CheckResult result, CheckResult previous, Clause clause, int index) {
    return resultFormatter.execute(result, previous, clause, index);
  }

  public String formatAssessment(Object value) {
    return assessmentFormatter.execute(value);
  }
}
