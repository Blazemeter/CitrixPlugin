package com.blazemeter.jmeter.citrix.clause.strategy.check;

import com.blazemeter.jmeter.citrix.clause.CheckResult;
import com.blazemeter.jmeter.citrix.clause.ClauseComputationException;
import com.blazemeter.jmeter.citrix.client.CitrixClient;
import com.blazemeter.jmeter.citrix.client.CitrixClient.Snapshot;
import com.blazemeter.jmeter.citrix.client.CitrixClientException;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides an abstract base class for {@link ClientChecker} that use screenshot
 * of Citrix session.
 */
public abstract class AbstractScreenChecker implements ClientChecker, ScreenshotAssessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractScreenChecker.class);

  private static final int MINIMUM_AREA_PIXELS = 2;

  private final ScreenshotAssessor screenshotAssessor;

  /**
   * Instantiates a new {@link AbstractScreenChecker}.
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
   * Gets the result a clause check from the specified snapshot.
   *
   * @param snapshot the snapshot to used to get screen info
   * @param context  the current polling context
   * @return the result a clause check from the specified snapshot
   * @throws ClauseComputationException when clause computation fails
   */
  protected CheckResult checkSnapshot(Snapshot snapshot, PollingContext context)
      throws ClauseComputationException {
    final Rectangle fgArea = snapshot.getForegroundWindowArea();

    // Get real area selection depending to foreground info
    Rectangle selection;
    try {
      selection = context.getAreaSelector().select(fgArea);
    } catch (Exception ex) {
      // Catch any exception and encapsulate it in ClauseComputationException
      String msg = MessageFormat.format("Unable to select area with foreground area {0}", fgArea);
      LOGGER.debug(msg, ex);
      throw new ClauseComputationException(msg, ex);
    }
    // Do screenshot assessment
    String value = assess(snapshot.getScreenshot(), selection, context);
    LOGGER.debug("Gets '{}' by evaluating the area {} of the screen", value, selection);
    return new CheckResult(snapshot, value, isSuccess(context, value));
  }

  /**
   * Indicates whether the specified result value is right taking into account the
   * polling context.
   *
   * @param context     the polling context
   * @param resultValue the value to test
   * @return true, if the specified result value is right taking into account the
   * polling context; false otherwise
   */
  protected abstract boolean isSuccess(PollingContext context, String resultValue);

  @Override
  public final CheckResult check(CitrixClient client, PollingContext context)
      throws CitrixClientException, ClauseComputationException {
    return checkSnapshot(client.takeSnapshot(), context);
  }

  @Override
  public String assess(BufferedImage image, Rectangle selection, PollingContext context)
      throws ClauseComputationException {

    if (image != null && selection != null) {
      if (selection.x >= image.getWidth() || selection.y >= image.getHeight()) {
        throw new ClauseComputationException("Selection area outside visible window");
      }
      if (selection.height < MINIMUM_AREA_PIXELS) {
        selection.height = MINIMUM_AREA_PIXELS;
      }
      if (selection.width < MINIMUM_AREA_PIXELS) {
        selection.height = MINIMUM_AREA_PIXELS;
      }
    }

    return screenshotAssessor.assess(image, selection, context);
  }

}
