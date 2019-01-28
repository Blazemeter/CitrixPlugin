package com.blazemeter.jmeter.citrix.assertion;

import java.io.Serializable;
import java.text.MessageFormat;

import org.apache.jmeter.assertions.Assertion;
import org.apache.jmeter.assertions.AssertionResult;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.AbstractTestElement;

import com.blazemeter.jmeter.citrix.clause.SessionState;
import com.blazemeter.jmeter.citrix.client.CitrixClient;
import com.blazemeter.jmeter.citrix.sampler.CitrixSessionHolder;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;

/**
 * Checks if a Citrix session is open / closed
 * 
 */
public class CitrixSessionAssertion extends AbstractTestElement implements Serializable, Assertion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3518438785145149054L;

	/** Key for storing expected state. */
	public static final String STATE_KEY = "CitrixSessionAssertion.state"; // $NON-NLS-1$
	private static final MessageFormat STATE_MESSAGE_FORMAT = new MessageFormat(
			CitrixUtils.getResString("citrix_assertion_session_state", false)); //$NON-NLS-1$

	/**
	 * Returns the result of the Assertion. Here it checks whether the Sample took
	 * to long to be considered successful. If so an AssertionResult containing a
	 * FailureMessage will be returned. Otherwise the returned AssertionResult will
	 * reflect the success of the Sample.
	 */
	@Override
	public AssertionResult getResult(SampleResult response) {
		AssertionResult result = new AssertionResult(getName());
		result.setFailure(false);
		CitrixClient citrixClient = CitrixSessionHolder.getClient();
		boolean connected = citrixClient != null && citrixClient.isConnected();
		boolean success = getExpectedState() == SessionState.OPEN ? connected : !connected;
		result.setFailure(!success);
		result.setFailureMessage(
				STATE_MESSAGE_FORMAT.format(new Object[] { getExpectedState(), connected ? SessionState.OPEN : SessionState.CLOSED }));
		return result;
	}

	/**
	 * @return {@link SessionState}
	 */
	public SessionState getExpectedState() {
		return SessionState.valueOf(getPropertyAsString(STATE_KEY, SessionState.OPEN.name()));
	}

	/**
	 * Set state
	 * 
	 * @param state {@link SessionState}
	 */
	public void setExpectedState(SessionState state) {
		setProperty(STATE_KEY, state.name());
	}
}
