package com.blazemeter.jmeter.citrix.sampler;

import org.apache.jmeter.samplers.SampleResult;
import com.blazemeter.jmeter.citrix.client.CitrixClient.Snapshot;

public class SamplerResultHelper {

	public static final String OK_CODE = "200";
	public static final String APPLICATION_EXIT_CODE = "204";

	private SamplerResultHelper() {

	}

	public static CitrixSampleResult createResult(CitrixBaseSampler sampler) {
		if (sampler == null) {
			throw new IllegalArgumentException("sampler must not be null.");
		}

		final CitrixSampleResult result = new CitrixSampleResult();
		result.setSampleLabel(sampler.getName());
		result.setDataType(SampleResult.BINARY);
		result.setEndClause(sampler.getEndClause());
		return result;
	}

	public static void setResultOk(CitrixSampleResult result) {
		if (result != null) {
			result.setResponseCode(OK_CODE);
			result.setSuccessful(true);
		}
	}

	public static CitrixSampleResult buildOkResult(CitrixBaseSampler sampler, Snapshot snapshot) {
		final CitrixSampleResult result = createResult(sampler);
		result.setSnapshot(snapshot);
		setResultOk(result);
		return result;
	}

}
