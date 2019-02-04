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

/**
 * This class provides an abstract base class for all Citrix samplers
 */
public abstract class CitrixBaseSampler extends AbstractSampler {

	private static final Logger LOGGER = LoggerFactory.getLogger(CitrixBaseSampler.class);

	private static final long serialVersionUID = 6412666498360532144L;

	private static final String END_CLAUSE_PROP = "CitrixBaseSampler.endClause"; // $NON-NLS-1$

	/**
	 * Lists the Citrix client states required to run the sampler
	 */
	protected enum RunningClientPolicy {
		/**
		 * Citrix client must not be running
		 */
		FORBIDDEN,

		/**
		 * Citrix client must be running
		 */
		REQUIRED;
	}

	private final RunningClientPolicy policy;

	/**
	 * Gets the state of the Citrix client required to run the sampler
	 * 
	 * @return the Citrix state policy
	 */
	public final RunningClientPolicy getRunningClientPolicy() {
		return policy;
	}

	/**
	 * Gets the end clause of the current sampler
	 * 
	 * @return the end clause if it exists; null otherwise
	 */
	public Clause getEndClause() {
		return (Clause) getProperty(END_CLAUSE_PROP).getObjectValue();
	}

	/**
	 * Defines the end clause used by the sampler to synchronize with Citrix session
	 * 
	 * @param clause the end clause to use; if null no synchronization runs
	 */
	public void setEndClause(Clause clause) {
		setProperty(new ObjectProperty(END_CLAUSE_PROP, clause));
	}

	/**
	 * Instantiates a new {@link CitrixBaseSampler}
	 * 
	 * @param policy the state of the Citrix client required to run the sampler
	 */
	protected CitrixBaseSampler(RunningClientPolicy policy) {
		if (policy == null) {
			throw new IllegalArgumentException("policy is required.");
		}
		this.policy = policy;
	}

	/**
	 * Creates a Citrix client listener for sampling execution
	 * 
	 * @return the Citrix client listener
	 */
	protected abstract SamplingHandler createHandler();

	/**
	 * Runs Citrix client operations while sampling
	 * 
	 * @param client the Citrix client used to run operations
	 * @throws SamplerRunException   when the sampling fails
	 * @throws CitrixClientException when the client fails to run operations
	 * @throws InterruptedException  when Citrix client operations are interrupted
	 */
	protected abstract void doClientAction(CitrixClient client)
			throws SamplerRunException, CitrixClientException, InterruptedException;

	// Instantiates a Citrix client and place it the session holder
	private CitrixClient createClient() throws SamplerRunException {
		try {
			CitrixClientFactory factory = CitrixClientFactoryHelper.getInstance();
			CitrixClient client = factory.createClient();
			CitrixSessionHolder.setClient(client);
			if (LOGGER.isDebugEnabled()) {
			    LOGGER.debug("{} on sampler {} has created the required Citrix client", getThreadName(), getName());
			}
			return client;
		} catch (CitrixClientFactoryException e) {
			String msg = MessageFormat.format(
					CitrixUtils.getResString("base_sampler_response_code_error_client_factory", false), e.getMessage());
			throw new SamplerRunException(msg);
		}
	}

	// Ensure the Citrix session is connected
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
		final boolean running = client.isRunning();
		if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug("For {} on sampler {}, Citrix client is running: {} whereas policy is {}", getThreadName(),
				getName(), running, policy);
		}
		if (policy == RunningClientPolicy.FORBIDDEN && running) {
			// A client is running whereas none is required
			throw new SamplerRunException(
					CitrixUtils.getResString("base_sampler_response_code_error_client_forbidden", false));
		} else if (policy == RunningClientPolicy.REQUIRED && !running) {
			// No client is running whereas one is required
			throw new SamplerRunException(
					CitrixUtils.getResString("base_sampler_response_code_error_client_required", false));
		}
	}

	private void handleClientAction(CitrixClient client, CitrixSampleResult result)
			throws SamplerRunException, InterruptedException {

		// Listen the Citrix client
		SamplingHandler handler = createHandler();
		client.addHandler(handler);
		if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug("{} on sampler {} starts listening Citrix client before sampling operations", getThreadName(),
				getName());
		}
		try {
			doClientAction(client);
		} catch (CitrixClientException e) {
			throw new SamplerRunException(
					CitrixUtils.getResString("base_sampler_response_code_error_client_action", false));
		} finally {
			client.removeHandler(handler);
			if (LOGGER.isDebugEnabled()) {
			    LOGGER.debug("{} stops listening Citrix client after sampling operations", getThreadName());
			}

			// Store Citrix client listener data in sample result
			result.setSamplerData(handler.getSamplerData());
		}

		// Throw exception if listener detects error events from Citrix client
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
			final CitrixClient client = CitrixSessionHolder.getClient();

			// Wait for end clause and register intermediate results
			if (LOGGER.isDebugEnabled()) {
			    LOGGER.debug("{} on sampler {} waits for end clause using {} strategy", getThreadName(), getName(),
					checkType);
			}
			try {
				boolean success = checkType.wait(clause, client, (checkResult, previous, i) -> {
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
					if(LOGGER.isTraceEnabled()) {
					    LOGGER.trace("{} on sampler {} gets check clause info: {}", getThreadName(), getName(), detail);
					}
					details.offer(detail);
				});

				if (!success) {
					throw new SamplerRunException(CitrixUtils.getResString("base_sampler_clause_timeout", false));
				} else {
					if (lastSnapshot[0] == null) {
					    if (LOGGER.isDebugEnabled()) {
					        LOGGER.debug("{} on sampler {} didn't get snapshot using strategy {}", getThreadName(),
								getName(), checkType);
					    }
						try {
							lastSnapshot[0] = client.takeSnapshot();
							if (LOGGER.isDebugEnabled()) {
							    LOGGER.debug("{} on sampler {} created a default snapshot", getThreadName(), getName(),
									checkType);
							}
						} catch (CitrixClientException ex) {
						    if (LOGGER.isInfoEnabled()) {
						        LOGGER.info("{} on sampler {} is not able to get default snapshot: {}", getThreadName(),
									getName(), ex.getMessage());
						    }
						    if (LOGGER.isDebugEnabled()) {
						        LOGGER.debug("Default snapshot for {} on sampler {} fail context:", getThreadName(),
									getName(), ex);
						    }
						}
					}
				}
			} finally {
				// Store registered results as response message
				result.setResponseMessage(SamplerHelper.formatResponseMessage(details, overflow[0]));

				// Store the last screenshot as response data
				result.setSnapshot(lastSnapshot[0]);
			}
		} else {
		    if (LOGGER.isDebugEnabled()) {
		        LOGGER.debug("{} does not wait for end clause because none is defined", getThreadName());
		    }
		}
	}

	@Override
	public final SampleResult sample(Entry e) {
		final CitrixSampleResult result = SamplerResultHelper.createResult(this);
		result.sampleStart();
		try {
			// Check the Citrix client state
			CitrixClient client = CitrixSessionHolder.getClient();
			if (client == null) {
				client = createClient();
			}
			ensurePolicyCompliance(client);

			// Do sampling operation on Citrix client
			handleClientAction(client, result);

			// Check the Citrix session is open
			ensureConnected(client);

			// Wait for end clause if it exists
			checkClause(result);

			// No exception so sampling is ok
			SamplerResultHelper.setResultOk(result);
		} catch (SamplerRunException ex) {
			// Store exception message in sample result
			result.setResponseCode(ex.getMessage());
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			result.setResponseCode(CitrixUtils.getResString("base_sampler_interrupted", false));
		} finally {
			// Stop sample
			result.sampleEnd();
		}
		return result;
	}

	protected abstract static class SamplingHandler extends SessionErrorLogger {

		private List<Integer> errors = new ArrayList<>();
        private String elementName;

		protected SamplingHandler(String elementName) {
		    this.elementName = elementName;
		}
		public String getSamplerData() {
			return "";
		}

		@Override
		public void handleSessionEvent(SessionEvent sessionEvent) {
			super.handleSessionEvent(sessionEvent);
			if (EventType.ERROR == sessionEvent.getEventType()) {
			    if (LOGGER.isDebugEnabled()) {
			        LOGGER.debug("{} on sampler {} detects Citrix session error with code={}", Thread.currentThread().getName(), elementName,
						sessionEvent.getErrorCode());
			    }
				synchronized (errors) {
					errors.add(Integer.valueOf(sessionEvent.getErrorCode()));
				}
			}
		}
	}
}
