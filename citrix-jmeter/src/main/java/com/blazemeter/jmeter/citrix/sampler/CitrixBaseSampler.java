package com.blazemeter.jmeter.citrix.sampler;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.property.ObjectProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blazemeter.jmeter.citrix.clause.CheckType;
import com.blazemeter.jmeter.citrix.clause.Clause;
import com.blazemeter.jmeter.citrix.client.CitrixClient;
import com.blazemeter.jmeter.citrix.client.CitrixClient.Snapshot;
import com.blazemeter.jmeter.citrix.client.CitrixClientException;
import com.blazemeter.jmeter.citrix.client.CitrixClientFactoryException;
import com.blazemeter.jmeter.citrix.client.CitrixClientFactoryHelper;
import com.blazemeter.jmeter.citrix.client.SessionErrorLogger;
import com.blazemeter.jmeter.citrix.client.events.SessionEvent;
import com.blazemeter.jmeter.citrix.client.events.SessionEvent.EventType;
import com.blazemeter.jmeter.citrix.client.factory.CitrixClientFactory;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;

public abstract class CitrixBaseSampler extends AbstractSampler {

	private static final Logger LOGGER = LoggerFactory.getLogger(CitrixBaseSampler.class);

	private static final long serialVersionUID = 6412666498360532144L;

	private static final String END_CLAUSE_PROP = "CitrixBaseSampler.endClause"; // $NON-NLS-1$

	protected enum RunningClientPolicy {
		FORBIDDEN, REQUIRED;
	}

	private final RunningClientPolicy policy;

	public final RunningClientPolicy getRunningClientPolicy() {
		return policy;
	}

	public Clause getEndClause() {
		return (Clause) getProperty(END_CLAUSE_PROP).getObjectValue();
	}

	public void setEndClause(Clause clause) {
		setProperty(new ObjectProperty(END_CLAUSE_PROP, clause));
	}

	protected CitrixBaseSampler(RunningClientPolicy policy) {
		if (policy == null) {
			throw new IllegalArgumentException("policy is required.");
		}
		this.policy = policy;
	}

	protected abstract SamplingHandler createHandler();

	protected abstract void doClientAction(CitrixClient client)
			throws SamplerRunException, CitrixClientException, InterruptedException;

	// Instantiates a Citrix client and place it the session holder
	private CitrixClient createClient() throws SamplerRunException {
		try {
			CitrixClientFactory factory = CitrixClientFactoryHelper.getInstance();
			CitrixClient client = factory.createClient();
			CitrixSessionHolder.setClient(client);
			return client;
		} catch (CitrixClientFactoryException e) {
			String msg = MessageFormat.format(
					CitrixUtils.getResString("base_sampler_response_code_error_client_factory", false), e.getMessage());
			throw new SamplerRunException(msg);
		}
	}

	private void ensureConnected(CitrixClient client) throws SamplerRunException {
		if (!client.isConnected()) {
			if (getEndClause() != null) {
				LOGGER.warn("{}: Cannot check end clause of sampler {} because remote application is terminated.",
						getThreadName(), getName());
			}
			throw new SamplerRunException(SamplerResultHelper.APPLICATION_EXIT_CODE);
		}
	}

	// Throws SamplerRunException if policy is not respected
	private void ensurePolicyCompliance(CitrixClient client) throws SamplerRunException {
		if (policy == RunningClientPolicy.FORBIDDEN && client.isRunning()) {
			// A client is running whereas none is required
			throw new SamplerRunException(
					CitrixUtils.getResString("base_sampler_response_code_error_client_forbidden", false));
		} else if (policy == RunningClientPolicy.REQUIRED && !client.isRunning()) {
			// No client is running whereas one is required
			throw new SamplerRunException(
					CitrixUtils.getResString("base_sampler_response_code_error_client_required", false));
		}
	}

	private void handleClientAction(CitrixClient client, CitrixSampleResult result)
			throws SamplerRunException, InterruptedException {
		SamplingHandler handler = createHandler();
		client.addHandler(handler);
		try {
			doClientAction(client);
		} catch (CitrixClientException e) {
			throw new SamplerRunException(
					CitrixUtils.getResString("base_sampler_response_code_error_client_action", false));
		} finally {
			client.removeHandler(handler);
			result.setSamplerData(handler.getSamplerData());
		}

		if (!handler.errors.isEmpty()) {
			throw new SamplerRunException(Integer.toString(handler.errors.get(0)));
		}
	}

	private void checkClause(final CitrixSampleResult result) throws SamplerRunException, InterruptedException {
		final Clause clause = getEndClause();
		if (clause != null) {
			final CheckType checkType = clause.getCheckType();
			final LinkedList<String> details = new LinkedList<>();
			final Snapshot[] lastSnapshot = new Snapshot[1];
			final boolean[] overflow = new boolean[] { false };

			final String checkErrorLabel = CitrixUtils.getResString("base_sampler_response_message_check_error", false);

			// Wait for end clause and register intermediate results
			try {
				boolean success = checkType.wait(clause, CitrixSessionHolder.getClient(),
						(checkResult, previous, i) -> {
							// Build response message
							String detail = checkType.name() + " #" + i + ": ";
							if (checkResult != null) {
								detail += checkType.format(checkResult, previous, clause, i);
								lastSnapshot[0] = checkResult.getSnapshot();
							} else {
								detail += checkErrorLabel;
							}
							
							// Handle too long message : remove first check details
							if (i > SamplerHelper.MAX_KEPT_CHECKS) {
								overflow[0] = true;
								details.remove();
							}
							LOGGER.trace("Check clause: {}", detail);
							details.offer(detail);
						});

				if (!success) {
					throw new SamplerRunException(CitrixUtils.getResString("base_sampler_clause_timeout", false));
				}
			} finally {
				// Store registered results as response message
				result.setResponseMessage(SamplerHelper.formatResponseMessage(details, overflow[0]));

				// Store the last screenshot as response data
				result.setSnapshot(lastSnapshot[0]);
			}
		}
	}

	@Override
	public final SampleResult sample(Entry e) {
		final CitrixSampleResult result = SamplerResultHelper.createResult(this);
		result.sampleStart();
		try {
			CitrixClient client = CitrixSessionHolder.getClient();
			if (client == null) {
				client = createClient();
			}
			ensurePolicyCompliance(client);
			handleClientAction(client, result);
			ensureConnected(client);
			checkClause(result);
			SamplerResultHelper.setResultOk(result);
		} catch (SamplerRunException ex) {
			result.setResponseCode(ex.getMessage());
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			result.setResponseCode(CitrixUtils.getResString("base_sampler_interrupted", false));
		} finally {
			result.sampleEnd();
		}
		return result;
	}

	protected abstract static class SamplingHandler extends SessionErrorLogger {

		private List<Integer> errors = new ArrayList<>();

		public String getSamplerData() {
			return "";
		}

		@Override
		public void handleSessionEvent(SessionEvent sessionEvent) {
			super.handleSessionEvent(sessionEvent);
			if (EventType.ERROR == sessionEvent.getEventType()) {
				synchronized (errors) {
					errors.add(Integer.valueOf(sessionEvent.getErrorCode()));
				}
			}
		}
	}
}
