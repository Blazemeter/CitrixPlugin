package com.blazemeter.jmeter.citrix.extractor;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.processor.PostProcessor;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.AbstractScopedTestElement;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blazemeter.jmeter.citrix.sampler.CitrixSampleResult;

public class AssessmentExtractor extends AbstractScopedTestElement implements PostProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(AssessmentExtractor.class);

	private static final long serialVersionUID = -293822356913142746L;

	private static final String PROP_DEFAULT_VALUE = "AssessmentExtractor.defaultValue";
	private static final String PROP_EMPTY_DEFAULT_VALUE = "AssessmentExtractor.emptyDefaultValue";
	private static final String PROP_REF_NAME = "AssessmentExtractor.refName";

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
				throw new IllegalArgumentException("Variable name is required for AssessmentExtractor " + getName());
			}

			// Get variable default value and put it in variable
			final String defaultValue = getDefaultValue();
			if (StringUtils.isNotBlank(defaultValue) || isDefaultValueEmpty()) {
				LOGGER.debug("Sets default value '{}' to variable {}", defaultValue, refName);
				vars.put(refName, defaultValue);
			}

			// Extract assessment if it exists
			String assessment = result.getAssessment();
			if (StringUtils.isNotBlank(assessment)) {
				LOGGER.debug("Sets value '{}' to variable {}", assessment, refName);
				vars.put(refName, assessment);
			}

		} else {
			LOGGER.debug("Only Citrix sample result are supported");
		}
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

}
