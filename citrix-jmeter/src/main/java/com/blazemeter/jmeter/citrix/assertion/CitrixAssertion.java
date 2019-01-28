package com.blazemeter.jmeter.citrix.assertion;

import java.io.Serializable;

import org.apache.jmeter.assertions.Assertion;
import org.apache.jmeter.assertions.AssertionResult;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.property.ObjectProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blazemeter.jmeter.citrix.clause.CheckResult;
import com.blazemeter.jmeter.citrix.clause.CheckType;
import com.blazemeter.jmeter.citrix.clause.Clause;
import com.blazemeter.jmeter.citrix.clause.ClauseComputationException;
import com.blazemeter.jmeter.citrix.client.CitrixClient.Snapshot;
import com.blazemeter.jmeter.citrix.sampler.CitrixSampleResult;

/**
 * Checks if the hash/ocr of a selected area from a sampler image is equal to a
 * hash/ocr given by the user.
 * 
 */
public class CitrixAssertion extends AbstractTestElement implements Serializable, Assertion {

	private static final Logger LOGGER = LoggerFactory.getLogger(CitrixAssertion.class);

	private static final long serialVersionUID = -3616999370321318753L;

	private static final String CLAUSE_PROP = "CitrixAssertion.clause"; // $NON-NLS-1$

	public static Clause createDefaultClause() {
		return new Clause(CheckType.HASH, "");
	}

	public Clause getClause() {
		return (Clause) getProperty(CLAUSE_PROP).getObjectValue();
	}

	public void setClause(Clause clause) {
		if (clause == null) {
			throw new IllegalArgumentException("clause must not be null");
		}
		setProperty(new ObjectProperty(CLAUSE_PROP, clause));
	}

	public CitrixAssertion() {
		setClause(createDefaultClause());
	}

	/**
	 * Returns the result of the Assertion. Checks if the sample result matchs the
	 * assertion inputs. If so an AssertionResult containing a FailureMessage will
	 * be returned. Otherwise the returned AssertionResult will reflect the success
	 * of the comparison.
	 */
	@Override
	public AssertionResult getResult(SampleResult response) {
		LOGGER.debug("process getResult from CitrixAssertion");
		AssertionResult result = new AssertionResult(getName());

		if (response instanceof CitrixSampleResult) {
			final CitrixSampleResult citrixResponse = (CitrixSampleResult) response;
			final Snapshot snapshot = citrixResponse.getSnapshot();
			if (snapshot != null) {
				final Clause clause = getClause();
				final CheckType checkType = clause.getCheckType();
				final String expectedValue = clause.getExpectedValue();
				try {
					CheckResult checkResult = checkType.checkSnapshot(clause, snapshot);
					if (!checkResult.isSuccessful()) {
						result.setResultForFailure(checkType.name() + " assertion: Expected value " + expectedValue
								+ " but got : " + checkResult.getValue());
					}
				} catch (ClauseComputationException e) {
					result.setError(true);
					result.setFailureMessage(e.getMessage());
					LOGGER.error("Error while checking image : {}", e.getMessage(), e);
				}
			} else {
				result.setResultForNull();
				LOGGER.warn("Could not execute assertion if there is no snapshot to process !");
			}
		}

		return result;
	}
}
