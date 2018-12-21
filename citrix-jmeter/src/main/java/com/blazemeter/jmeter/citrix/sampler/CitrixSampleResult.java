package com.blazemeter.jmeter.citrix.sampler;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blazemeter.jmeter.citrix.clauses.Clause;
import com.blazemeter.jmeter.citrix.clauses.ClauseHelper;
import com.blazemeter.jmeter.citrix.client.CitrixClient.Snapshot;

/**
 * Provides a {@link SampleResult} dedicated to Citrix samplers
 */
public class CitrixSampleResult extends SampleResult {

	private static final Logger LOGGER = LoggerFactory.getLogger(CitrixSampleResult.class);

	private static final long serialVersionUID = 639689516573625176L;

	private transient Snapshot snapshot;

	private Rectangle fgWindowArea;
	private Clause endClause;

	/**
	 * Gets the snapshot of the Citrix client during sample
	 * 
	 * @return the snapshot
	 */
	public Snapshot getSnapshot() {
		if (snapshot == null) {
			try {
				BufferedImage screenshot = ClauseHelper.convertByteArrayToImage(getResponseData());
				snapshot = new Snapshot(screenshot, fgWindowArea);
			} catch (IOException e) {
				LOGGER.error("Unable to build snapshot from fgWindowArea:{}", fgWindowArea, e);
				return null;
			}
		}
		return snapshot;
	}

	/**
	 * Defines the snapshot of the Citrix client during sample
	 * 
	 * @param snapshot the snapshot to set
	 */
	public void setSnapshot(Snapshot snapshot) {
		this.snapshot = snapshot;

		// Compute data for storage
		if (snapshot != null) {
			this.fgWindowArea = snapshot.getForegroundWindowArea();
			BufferedImage screenshot = snapshot.getScreenshot();
			if (screenshot != null) {
				try {
					byte[] data = ClauseHelper.convertImageToByteArray(screenshot);
					super.setResponseData(data);
				} catch (IOException e) {
					LOGGER.error("Unable to build response message from screenshot.", e);
					setResponseData(new byte[0]);
				}
			}
		}
	}

	/**
	 * Gets the foreground window area during sample
	 * 
	 * @return the foreground window area during sample
	 */
	public Rectangle getFgWindowArea() {
		return fgWindowArea;
	}

	/**
	 * Defines the foreground window area during sample
	 * 
	 * @param fgWindowArea the foreground window area to set
	 */
	void setFgWindowArea(Rectangle fgWindowArea) {
		this.fgWindowArea = fgWindowArea;
	}

	/**
	 * Gets the end clause of the Citrix sampler that generated this result
	 * 
	 * @return the end clause of the Citrix sampler that generated this result
	 */
	public Clause getEndClause() {
		return endClause;
	}

	/**
	 * Defines the end clause of the Citrix sampler that generated this result
	 * 
	 * @param endClause the end clause to set
	 */
	public void setEndClause(Clause endClause) {
		this.endClause = endClause;
	}
}
