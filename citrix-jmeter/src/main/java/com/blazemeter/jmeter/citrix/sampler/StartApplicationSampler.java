package com.blazemeter.jmeter.citrix.sampler;

import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.testelement.TestIterationListener;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.testelement.ThreadListener;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blazemeter.jmeter.citrix.client.CitrixClient;
import com.blazemeter.jmeter.citrix.client.CitrixClientException;
import com.blazemeter.jmeter.citrix.client.events.SessionEvent;
import com.blazemeter.jmeter.citrix.client.events.SessionEvent.EventType;
import com.blazemeter.jmeter.citrix.ocr.OcrManagerHolder;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;

/**
 * Special sampler in charge of starting remote application.
 */
public class StartApplicationSampler extends CitrixBaseSampler
		implements ThreadListener, TestStateListener, TestIterationListener {

	private static final long serialVersionUID = 1141779171595666976L;

	private static final Logger LOGGER = LoggerFactory.getLogger(StartApplicationSampler.class);

	// JMeterProperty that defines the maximum time to receive a Citrix session
	// event of type LOGON when Citrix application starts
	private static final long LOGON_TIMEOUT = JMeterUtils.getPropDefault(CitrixUtils.PROPERTIES_PFX + "logon_timeout",
			20000); // $NON-NLS-1$

	private static final boolean FORCE_NORMAL_MODE = JMeterUtils.getPropDefault(CitrixUtils.PROPERTIES_PFX + "force_normal_output", false); // $NON-NLS-1$
	private static final boolean KEEP_ICA_FILES = JMeterUtils.getPropDefault(CitrixUtils.PROPERTIES_PFX + "keep_ica_files", false); // $NON-NLS-1$

	private static final String LOGON_TIMEOUT_PROP = "StartApplicationSampler.logonTimeout";
	private static final String FILE_PATH_VAR_PROP = "StartApplicationSampler.filePathVar";

	private transient CountDownLatch userLogged = new CountDownLatch(1);
	
	private AtomicBoolean logged = new AtomicBoolean(false);

	/**
	 * Gets the maximum waiting time before getting the confirmation that the Citrix
	 * user is logged in.
	 * <p>
	 * This time is used to ensure Citrix client is in a right state before checking
	 * end clause.
	 * 
	 * @return the maximum waiting time before getting the confirmation that the
	 *         Citrix user
	 */
	public long getLogOnTimeout() {
		return getPropertyAsLong(LOGON_TIMEOUT_PROP);
	}

	/**
	 * Gets a string that represents the maximum waiting time before getting the
	 * confirmation that the Citrix user is logged in.
	 * <p>
	 * See {@link StartApplicationSampler#getLogOnTimeout()}
	 * 
	 * @return a string that represents the maximum waiting time before getting the
	 *         confirmation that the Citrix user
	 */
	public String getLogOnTimeoutAsString() {
		return getPropertyAsString(LOGON_TIMEOUT_PROP);
	}

	/**
	 * Defines the maximum waiting time before getting the confirmation that the
	 * Citrix user is logged in
	 * 
	 * @param timeout the maximum waiting time before getting the confirmation that
	 *                the Citrix user is logged in
	 */
	public void setLogOnTimeout(String timeout) {
		setProperty(LOGON_TIMEOUT_PROP, timeout);
	}

	public String getICAPathVar() {
		return getPropertyAsString(FILE_PATH_VAR_PROP);
	}

	public void setICAPathVar(String filePathVar) {
		setProperty(FILE_PATH_VAR_PROP, filePathVar);
	}

	/**
	 * Instantiates a new {@link StartApplicationSampler}.
	 * <p>
	 * This sampler requires that no Citrix client is already running
	 */
	public StartApplicationSampler() {
		super(RunningClientPolicy.FORBIDDEN);
	}

	private boolean waitLogOn(long timeout) throws InterruptedException {
		boolean expired = false;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{} on sampler {} starts to wait for Logon event at {}", Thread.currentThread().getName(), getName(),
					System.currentTimeMillis());
		}
		if (!logged.get()) {
			expired = !userLogged.await(timeout, TimeUnit.MILLISECONDS);
		}
		if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug("{} stops to wait for Logon event at {}, timeout expired: {}", Thread.currentThread().getName(),
				System.currentTimeMillis(), expired);
		}
		return expired;
	}

	@Override
	protected SamplingHandler createHandler() {
		return new SamplingHandler(getName()) {

			@Override
			public String getSamplerData() {
				return "Start Citrix application using " + getICAPathVar();
			}

			@Override
			public void handleSessionEvent(SessionEvent sessionEvent) {
				super.handleSessionEvent(sessionEvent);
				if (sessionEvent.getEventType() == EventType.LOGON) {
					LOGGER.debug("{} on sampler {} detects Citrix session logon", Thread.currentThread().getName(), getName());
					logged.compareAndSet(false, true);
					userLogged.countDown();
				}
			}

		};
	}

	@Override
	protected void doClientAction(CitrixClient client)
			throws CitrixClientException, SamplerRunException, InterruptedException {
		String icaFilePath = getThreadContext().getVariables().get(getICAPathVar());
		try {
    		if (icaFilePath == null) {
    			throw new IllegalStateException("ICA file path is null for variable:" + getICAPathVar());
    		}
    		client.setICAFilePath(Paths.get(icaFilePath));
    
    		long timeout = getLogOnTimeout();
    		if (timeout <= 0) {
    			timeout = LOGON_TIMEOUT;
    		}
    
    		// Run Citrix client and wait for Logon event
    		LOGGER.debug("{} on sampler {} launches a Citrix session", Thread.currentThread().getName(), getName());
    		client.start(true, FORCE_NORMAL_MODE ? true : GuiPackage.getInstance() != null);
    		boolean expired = waitLogOn(timeout);
    		if (expired) {
    			throw new SamplerRunException(MessageFormat
    					.format(CitrixUtils.getResString("start_application_sampler_logon_timeout_fmt", false), timeout));
    		}
    		if(!client.isConnected()) {
    		    throw new SamplerRunException(CitrixUtils.getResString("start_application_sampler_not_connected", false));
    		}
		} finally {
		    if (!KEEP_ICA_FILES && icaFilePath != null &&  !Paths.get(icaFilePath).toFile().delete()) {
		        LOGGER.warn("Cannot delete ica file {}", icaFilePath);
		    }
		}
	}

	@Override
	public void threadStarted() {
		LOGGER.info("{} on sampler {} ensures OCR is initialized", Thread.currentThread().getName(), getName());
		long start = System.currentTimeMillis();
		OcrManagerHolder.getManager();
		LOGGER.info("OCR initialized in {} millis", System.currentTimeMillis() - start);
	}

	private void resetClient() {
		CitrixClient client = CitrixSessionHolder.getClient();
		if (client != null && client.isRunning()) {
			try {
				client.stop();
			} catch (CitrixClientException e) {
				LOGGER.error("{} on sampler {} is unable to stop running Citrix client.", Thread.currentThread().getName(), getName(),
						e);
			}
			CitrixSessionHolder.setClient(null);
			logged.set(false);
			userLogged = new CountDownLatch(1);
		}
	}

	@Override
	public void threadFinished() {
		resetClient();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.jmeter.testelement.TestStateListener#testStarted()
	 */
	@Override
	public void testStarted() {
		testStarted("");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.jmeter.testelement.TestStateListener#testStarted(java.lang.String)
	 */
	@Override
	public void testStarted(String host) {
		// NOOP
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.jmeter.testelement.TestStateListener#testEnded()
	 */
	@Override
	public void testEnded() {
		testEnded(""); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.jmeter.testelement.TestStateListener#testEnded(java.lang.String)
	 */
	@Override
	public void testEnded(String host) {
		// NOOP
	}

	@Override
	public void testIterationStart(LoopIterationEvent event) {
		resetClient();
	}
}
