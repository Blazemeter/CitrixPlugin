package com.blazemeter.jmeter.citrix.gui;

import com.blazemeter.jmeter.citrix.clause.Clause;
import com.blazemeter.jmeter.citrix.clause.ClauseHelper;
import com.blazemeter.jmeter.citrix.clause.gui.ClauseBuilderPanel;
import com.blazemeter.jmeter.citrix.sampler.CitrixSampleResult;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.JTabbedPane;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.visualizers.ResultRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides {@link ResultRenderer} dedicated to {@link CitrixSampleResult}.
 */
public class CitrixResultRenderer implements ResultRenderer {

  private static final Logger LOGGER = LoggerFactory.getLogger(CitrixResultRenderer.class);

  private ClauseBuilderPanel pnlBuilder;

  private JTabbedPane parent;
  private SampleResult result;

  @Override
  public void clearData() {
    // Reset UI
    pnlBuilder.setImage(null);
    pnlBuilder.setFgWindowArea(null);
    pnlBuilder.setSelection(null, false);
  }

  @Override
  public void init() {
    // Init UI
    pnlBuilder = new ClauseBuilderPanel();
  }

  @Override
  public void setupTabPane() {
    if (parent.indexOfComponent(pnlBuilder) < 0) {
      parent.addTab(CitrixUtils.getResString("result_renderer_title", false), pnlBuilder);
    }
    clearData();
  }

  @Override
  public void setLastSelectedTab(int index) {
    // NOOP
  }

  @Override
  public void setRightSide(JTabbedPane rightSide) {
    parent = rightSide;
  }

  @Override
  public void setSamplerResult(Object userObject) {
    if (userObject instanceof SampleResult) {
      result = (SampleResult) userObject;
    }

  }

  @Override
  public void renderResult(SampleResult sampleResult) {
    renderImage(sampleResult);
  }

  @Override
  public void renderImage(SampleResult sampleResult) {
    // Use image stored in sample result as byte array
    BufferedImage image = null;
    try {
      image = ClauseHelper.convertByteArrayToImage(result.getResponseData());
    } catch (IOException e) {
      LOGGER.error("Unable to get image from response data", e);
    }

    // Get result data
    Rectangle fgWindowArea = null;
    Rectangle selection = null;
    boolean relative = false;
    if (sampleResult instanceof CitrixSampleResult) {
      CitrixSampleResult citrixSampleResult = (CitrixSampleResult) sampleResult;
      fgWindowArea = citrixSampleResult.getFgWindowArea();

      Clause clause = citrixSampleResult.getEndClause();
      if (clause != null && clause.getCheckType().isSupportingSelection()) {
        selection = clause.getSelection();
        relative = clause.isRelative();
      }
    }

    // Update UI with result data
    pnlBuilder.setImage(image);
    pnlBuilder.setFgWindowArea(fgWindowArea);
    pnlBuilder.setSelection(selection, relative);
  }

  @Override
  public void setBackgroundColor(Color backGround) {
    // NOOP
  }

  @Override
  public String toString() {
    return CitrixUtils.getResString("result_renderer_title", false);
  }

}
