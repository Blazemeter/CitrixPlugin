package com.blazemeter.jmeter.citrix.sampler;

import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

	private static final String LOGON_TIMEOUT_PROP = "StartApplicationSampler.logonTimeout";
	private static final String FILE_PATH_VAR_PROP = "StartApplicationSampler.filePathVar";

	private final transient Lock locker = new ReentrantLock();
	private final transient Condition userLogged = locker.newCondition();
	private boolean logged = false;

	public long getLogOnTimeout() {
		return getPropertyAsLong(LOGON_TIMEOUT_PROP);
	}

	public String getLogOnTimeoutAsString() {
		return getPropertyAsString(LOGON_TIMEOUT_PROP);
	}

	public void setLogOnTimeout(String timeout) {
		setProperty(LOGON_TIMEOUT_PROP, timeout);
	}

	public String getICAPathVar() {
		return getPropertyAsString(FILE_PATH_VAR_PROP);
	}

	public void setICAPathVar(String filePathVar) {
		setProperty(FILE_PATH_VAR_PROP, filePathVar);
	}

	public StartApplicationSampler() {
		super(RunningClientPolicy.FORBIDDEN);
	}

	private boolean waitLogOn(long timeout) throws InterruptedException {
		boolean expired = false;
		try {
			locker.lock();
			while (!logged && !expired) {
				expired = !userLogged.await(timeout, TimeUnit.MILLISECONDS);
			}
		} finally {
			locker.unlock();
		}
		return expired;
	}

	@Override
	protected SamplingHandler createHandler() {
		return new SamplingHandler() {

			@Override
			public String getSamplerData() {
				return "Start Citrix application using " + getICAPathVar();
			}

			@Override
			public void handleSessionEvent(SessionEvent sessionEvent) {
				super.handleSessionEvent(sessionEvent);
				if (sessionEvent.getEventType() == EventType.LOGON) {
					try {
						locker.lock();
						logged = true;
						userLogged.signal();
					} finally {
						locker.unlock();
					}
				}
			}

		};
	}

	@Override
	protected Long doClientAction(CitrixClient client)
			throws CitrixClientException, SamplerRunException, InterruptedException {
		String icaFilePath = getThreadContext().getVariables().get(getICAPathVar());
		if (icaFilePath == null) {
			throw new IllegalStateException("ICA file path is null for variable:" + getICAPathVar());
		}
		client.setICAFilePath(Paths.get(icaFilePath));

		long timeout = getLogOnTimeout();
		if (timeout <= 0) {
			timeout = LOGON_TIMEOUT;
		}

		client.start(true, GuiPackage.getInstance() != null);
		boolean expired = waitLogOn(timeout);
		if (expired) {
			throw new SamplerRunException(MessageFormat
					.format(CitrixUtils.getResString("start_application_sampler_logon_timeout_fmt", false), timeout));

		}
		return System.currentTimeMillis();
	}

	@Override
	public void threadStarted() {
		LOGGER.info("Ensuring OCR is initialized");
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
				LOGGER.error("Unable to stop running Citrix client.", e);
			}
			CitrixSessionHolder.setClient(null);
			try {
				locker.lock();
				logged = false;
			} finally {
				locker.unlock();
			}
			
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
