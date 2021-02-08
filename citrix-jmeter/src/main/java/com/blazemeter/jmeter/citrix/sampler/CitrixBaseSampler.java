package com.blazemeter.jmeter.citrix.sampler;

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
import com.blazemeter.jmeter.citrix.client.factory.AbstractCitrixClientFactory;
import com.blazemeter.jmeter.citrix.sampler.SamplerRunException.ErrorCode;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
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

/**
 * This class provides an abstract base class for all Citrix samplers.
 */
public abstract class CitrixBaseSampler extends AbstractSampler {

  private static final Logger LOGGER = LoggerFactory.getLogger(CitrixBaseSampler.class);

  private static final long serialVersionUID = 6412666498360532144L;

  private static final String END_CLAUSE_PROP = "CitrixBaseSampler.endClause"; // $NON-NLS-1$

  public AbstractCitrixClientFactory factory;

  private final RunningClientPolicy policy;


  /**
   * Instantiates a new {@link CitrixBaseSampler}.
   *
   * @param policy the state of the Citrix client required to run the sampler
   */
  protected CitrixBaseSampler(RunningClientPolicy policy) {
    try {
      factory = (AbstractCitrixClientFactory) CitrixClientFactoryHelper.getInstance();
    } catch (CitrixClientFactoryException e) {
      String msg = MessageFormat.format(
          CitrixUtils.getResString("base_sampler_response_code_error_client_factory", false),
          e.getMessage());
      throw new RuntimeException(msg, e);
    }
    if (policy == null) {
      throw new IllegalArgumentException("policy is required.");
    }
    this.policy = policy;
  }

  /**
   * Gets the state of the Citrix client required to run the sampler.
   *
   * @return the Citrix state policy
   */
  public final RunningClientPolicy getRunningClientPolicy() {
    return policy;
  }

  /**
   * Gets the end clause of the current sampler.
   *
   * @return the end clause if it exists; null otherwise
   */
  public Clause getEndClause() {
    return (Clause) getProperty(END_CLAUSE_PROP).getObjectValue();
  }

  /**
   * Defines the end clause used by the sampler to synchronize with Citrix session.
   *
   * @param clause the end clause to use; if null no synchronization runs
   */
  public void setEndClause(Clause clause) {
    setProperty(new ObjectProperty(END_CLAUSE_PROP, clause));
  }

  /**
   * Creates a Citrix client listener for sampling execution.
   *
   * @return the Citrix client listener
   */
  protected abstract SamplingHandler createHandler();

  /**
   * Runs Citrix client operations while sampling.
   *
   * @param client the Citrix client used to run operations
   * @throws SamplerRunException   when the sampling fails
   * @throws CitrixClientException when the client fails to run operations
   * @throws InterruptedException  when Citrix client operations are interrupted
   */
  protected abstract void doClientAction(CitrixClient client)
      throws SamplerRunException, CitrixClientException, InterruptedException;

  public void initClient(CitrixClient client) {

  }

  // Instantiates a Citrix client and place it the session holder
  private CitrixClient createClient() throws SamplerRunException {
    CitrixClient client = factory.createClient();
    initClient(client);
    CitrixSessionHolder.setClient(client);
    LOGGER.debug("On sampler {} has created the required Citrix client", getName());
    return client;
  }

  // Ensure the Citrix session is connected
  private void ensureConnected(CitrixClient client) throws SamplerRunException {
    if (!client.isConnected()) {
      if (getEndClause() != null) {
        LOGGER
            .warn("Cannot check end clause of sampler {} because remote application is terminated.",
                getName());
      }
      throw new SamplerRunException(ErrorCode.CLAUSE_LOST_CONNECTION,
          SamplerResultHelper.APPLICATION_EXIT_CODE);
    }
  }

  // Throws SamplerRunException if policy is not respected
  private void ensurePolicyCompliance(CitrixClient client) throws SamplerRunException {
    final boolean running = client.isRunning();
    LOGGER.debug("On sampler {}, Citrix client is running: {} whereas policy is {}",
        getName(), running, policy);

    if (policy == RunningClientPolicy.FORBIDDEN && running) {
      // A client is running whereas none is required
      throw new SamplerRunException(ErrorCode.POLICY_ERROR,
          CitrixUtils.getResString("base_sampler_response_code_error_client_forbidden", false));
    } else if (policy == RunningClientPolicy.REQUIRED && !running) {
      // No client is running whereas one is required
      throw new SamplerRunException(ErrorCode.POLICY_ERROR,
          CitrixUtils.getResString("base_sampler_response_code_error_client_required", false));
    }
  }

  private void handleClientAction(CitrixClient client, CitrixSampleResult result)
      throws SamplerRunException, InterruptedException {

    // Listen the Citrix client
    SamplingHandler handler = createHandler();
    client.addHandler(handler);
    LOGGER.debug("On sampler {} starts listening Citrix client before sampling operations",
        getName());
    try {
      doClientAction(client);
    } catch (CitrixClientException e) {
      throw new SamplerRunException(e.code(), e.getMessage(), e);
    } catch (Exception e) {
      throw new SamplerRunException(CitrixClientException.ErrorCode.UNKNOWN_ERROR.toString(),
          e.getMessage(), e);
    } finally {
      client.removeHandler(handler);
      LOGGER.debug("Stops listening Citrix client after sampling operations");

      // Store Citrix client listener data in sample result
      result.setSamplerData(handler.getSamplerData());
    }

    // Throw exception if listener detects error events from Citrix client
    if (!handler.errors.isEmpty()) {
      throw new SamplerRunException(ErrorCode.CLIENT_ERROR,
          Integer.toString(handler.errors.get(0)));
    }
  }

  private void checkClause(final CitrixSampleResult result)
      throws SamplerRunException, InterruptedException {
    final Clause clause = getEndClause();
    if (clause != null) {
      final CheckType checkType = clause.getCheckType();
      final LinkedList<String> details = new LinkedList<>();
      final String[] assessment = new String[1];
      final Snapshot[] lastSnapshot = new Snapshot[1];
      final boolean[] overflow = new boolean[] {false};
      final String checkErrorLabel =
          CitrixUtils.getResString("base_sampler_response_message_check_error", false);
      final CitrixClient client = CitrixSessionHolder.getClient();

      // Wait for end clause and register intermediate results
      LOGGER.debug("On sampler {} waits for end clause using {} strategy", getName(), checkType);

      boolean success = false;

      try {
        success = checkType.wait(clause, client, (checkResult, previous, i) -> {
          // Build response message
          String detail = checkType.name() + " #" + i + ": ";
          if (checkResult != null) {
            detail += checkType.formatResult(checkResult, previous, clause, i);
            lastSnapshot[0] = checkResult.getSnapshot();

            // Store success assessment if it exists
            if (checkResult.isSuccessful()) {
              assessment[0] = checkType.formatAssessment(checkResult.getValue());
            }
          } else {
            detail += checkErrorLabel;
          }

          // Handle too long message : remove first check details
          if (i > SamplerHelper.MAX_KEPT_CHECKS) {
            overflow[0] = true;
            details.remove();
          }
          LOGGER.trace("On sampler {} gets check clause info: {}", getName(), detail);
          details.offer(detail);
        });

        if (lastSnapshot[0] == null) {
          LOGGER
              .debug("On sampler {} didn't get snapshot using strategy {}", getName(), checkType);
          try {
            lastSnapshot[0] = client.takeSnapshot();
            LOGGER.debug("On sampler {} created a default snapshot {}", getName(), checkType);
          } catch (CitrixClientException ex) {
            LOGGER.warn("On sampler {} is not able to get default snapshot: {}", getName(),
                ex);
          }
        }
      } finally {
        // Store success assessment in sample result
        result.setAssessment(assessment[0]);

        // Store the last screenshot as response data
        result.setSnapshot(lastSnapshot[0]);
      }
      if (!success) {
        throw new SamplerRunException(ErrorCode.CLAUSE_TIMEOUT,
            CitrixUtils.getResString("base_sampler_clause_timeout", false));
      }
    } else {
      LOGGER.debug("Does not wait for end clause because none is defined");
    }
  }

  @Override
  public final SampleResult sample(Entry e) {
    final CitrixSampleResult result = SamplerResultHelper.createResult(this);
    result.sampleStart();
    // Check the Citrix client state
    CitrixClient client = CitrixSessionHolder.getClient();
    try {
      if (client == null) {
        LOGGER.debug("Create Client");
        client = createClient();
      } else {
        LOGGER.debug("Use Client from CitrixSessionHolder");
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
      result.setResponseCode(ex.code());
      result.setResponseMessage(ex.getMessage());
      LOGGER.error("Sample error {}", ex.code(), ex);
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      LOGGER.debug("Sample interrupted", ex);
      result.setResponseCode(CitrixUtils.getResString("base_sampler_interrupted", false));
    } finally {
      if (client != null && !result.hasSnapshot()) {
        try {
          result.setSnapshot(client.takeSnapshot());
        } catch (CitrixClientException ex) {
          LOGGER.warn("Unable to add snapshot to sampler without one on {}",
              getName());
        }
      }

      // Stop sample
      result.sampleEnd();
    }
    return result;
  }

  /**
   * Lists the Citrix client states required to run the sampler.
   */
  protected enum RunningClientPolicy {
    /**
     * Citrix client must not be running.
     */
    FORBIDDEN,

    /**
     * Citrix client must be running.
     */
    REQUIRED
  }

  protected abstract static class SamplingHandler extends SessionErrorLogger {

    private final List<Integer> errors = new ArrayList<>();
    private final String elementName;

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
        LOGGER.debug("On sampler {} detects Citrix session error with code={}", elementName,
            sessionEvent.getErrorCode());
        synchronized (errors) {
          errors.add(sessionEvent.getErrorCode());
        }
      }
    }
  }
}

