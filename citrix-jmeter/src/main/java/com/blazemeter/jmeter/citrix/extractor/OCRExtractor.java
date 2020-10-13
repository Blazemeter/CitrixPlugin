package com.blazemeter.jmeter.citrix.extractor;

import com.blazemeter.jmeter.citrix.clause.ClauseComputationException;
import com.blazemeter.jmeter.citrix.clause.ClauseHelper;
import com.blazemeter.jmeter.citrix.client.CitrixClient.Snapshot;
import com.blazemeter.jmeter.citrix.sampler.CitrixSampleResult;
import java.awt.Rectangle;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.processor.PostProcessor;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.AbstractScopedTestElement;
import org.apache.jmeter.testelement.property.ObjectProperty;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OCRExtractor extends AbstractScopedTestElement implements PostProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(OCRExtractor.class);

  private static final long serialVersionUID = 8042933229778741660L;

  private static final String PROP_RELATIVE = "ScreenExtractor.relative"; // $NON-NLS-1$
  private static final String PROP_SELECTION = "ScreenExtractor.selection"; // $NON-NLS-1$
  private static final String PROP_DEFAULT_VALUE = "ScreenExtractor.defaultValue";
  private static final String PROP_EMPTY_DEFAULT_VALUE = "ScreenExtractor.emptyDefaultValue";
  private static final String PROP_REF_NAME = "ScreenExtractor.refName";

  @Override
  public void process() {
    JMeterContext context = getThreadContext();
    SampleResult previousResult = context.getPreviousResult();
    if (previousResult instanceof CitrixSampleResult) {
      LOGGER.debug("Working on Citrix sample result");
      CitrixSampleResult result = (CitrixSampleResult) previousResult;
      JMeterVariables vars = context.getVariables();

      // Get variable name to populate
      final String refName = getRefName();
      if (StringUtils.isBlank(refName)) {
        throw new IllegalArgumentException(
            "Variable name is required for ScreenExtractor " + getName());
      }

      // Get variable default value and put it in variable
      final String defaultValue = getDefaultValue();
      if (StringUtils.isNotBlank(defaultValue) || isDefaultValueEmpty()) {
        LOGGER.debug("Sets default value '{}' to variable {}", defaultValue, refName);
        vars.put(refName, defaultValue);
      }

      // Extract value if pre-requisites are ok
      final Snapshot snapshot = result.getSnapshot();
      if (snapshot != null) {
        try {
          Rectangle area = ClauseHelper.getAbsoluteSelection(getSelection(), isRelative(),
              snapshot.getForegroundWindowArea());
          String value = ClauseHelper.recognize(snapshot.getScreenshot(), area, null);
          LOGGER.debug("Gets '{}' from OCR engine in area {}", value, area);
          if (StringUtils.isNotBlank(value)) {
            LOGGER.debug("Sets value '{}' to variable {}", value, refName);
            vars.put(refName, value);
          }
        } catch (ClauseComputationException ex) {
          LOGGER.error("Error while extracting value from screenshot : {}", ex.getMessage(), ex);
        }
      }
    } else {
      LOGGER.debug("Only Citrix sample result are supported");
    }
  }

  public boolean isRelative() {
    return getPropertyAsBoolean(PROP_RELATIVE);
  }

  public void setRelative(boolean relative) {
    setProperty(PROP_RELATIVE, relative);
  }

  public String getDefaultValue() {
    return getPropertyAsString(PROP_DEFAULT_VALUE);
  }

  public void setDefaultValue(String defaultValue) {
    setProperty(PROP_DEFAULT_VALUE, defaultValue);
  }

  public boolean isDefaultValueEmpty() {
    return getPropertyAsBoolean(PROP_EMPTY_DEFAULT_VALUE);
  }

  public void setEmptyDefaultValue(boolean emptyDefaultValue) {
    setProperty(PROP_EMPTY_DEFAULT_VALUE, emptyDefaultValue);
  }

  public String getRefName() {
    return getPropertyAsString(PROP_REF_NAME);
  }

  public void setRefName(String refName) {
    setProperty(PROP_REF_NAME, refName);
  }

  public Rectangle getSelection() {
    return (Rectangle) getProperty(PROP_SELECTION).getObjectValue();
  }

  public void setSelection(Rectangle selection) {
    setProperty(new ObjectProperty(PROP_SELECTION, selection));
  }

}
