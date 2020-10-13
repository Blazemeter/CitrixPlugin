package com.blazemeter.jmeter.citrix.clause.strategy.check;

import com.blazemeter.jmeter.citrix.clause.CheckResult;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a {@link ClientChecker} that can detect difference on Citrix session
 * screen.
 */
public class DiffChecker extends AbstractScreenChecker {

  private static final Logger LOGGER = LoggerFactory.getLogger(DiffChecker.class);

  /**
   * Instantiates a new {@link DiffChecker}.
   *
   * @param imageAssessor the function called to do screenshot assessment
   */
  public DiffChecker(ScreenshotAssessor imageAssessor) {
    super(imageAssessor);
  }

  @Override
  protected boolean isSuccess(PollingContext context, String resultValue) {
    // Compare with the previous result if it exists
    final CheckResult previous = context.getPrevious();
    final boolean result = previous != null && !Objects.equals(resultValue, previous.getValue());
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("'{}' differs from previous result '{}': {}", resultValue,
          (previous != null ? previous.getValue() : null), result);
    }

    return result;
  }

}
