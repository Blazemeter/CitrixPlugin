package com.blazemeter.jmeter.citrix.utils;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Similar work as JMeterUtils. Used to get statics Strings used by the plugin.
 * Theses Strings are stored in messages_loc.properties files.
 */
public class CitrixUtils {

	public static final String PROPERTIES_PFX = "bzm.citrix.";

	private static final Logger LOGGER = LoggerFactory.getLogger(CitrixUtils.class);

	private CitrixUtils() {
	}

	/**
	 * Give the corresponding String to the given key. If isJMeterNative is true,
	 * return the String contained in the JMeter.properties files. Else, give the
	 * String contained in the Citrix.properties files.
	 * 
	 * @param key            property used to find the corresponding String
	 * @param isJMeterNative tell if the key is in the JMeter properties files
	 * @return the corresponding String to the key in the messages_loc.properties
	 *         files
	 */
	public static String getResString(String key, boolean isJMeterNative) {
		ResourceBundle resources;
		if (isJMeterNative) {
			return JMeterUtils.getResString(key);
		}

		String resKey = key.replace(' ', '_');
		resKey = resKey.toLowerCase(java.util.Locale.ENGLISH);
		String resString = null;

		try {
			Locale loc = JMeterUtils.getLocale();
			resources = ResourceBundle.getBundle("com.blazemeter.jmeter.citrix.resources.messages", loc);

			ResourceBundle bundle = resources;

			if (bundle.containsKey(resKey)) {
				resString = bundle.getString(resKey);
			} else {
				LOGGER.warn("ERROR! Resource string not found: [{}]", resKey);
				resString = JMeterUtils.RES_KEY_PFX + key + "]";
			}
		} catch (MissingResourceException mre) {
			LOGGER.warn("ERROR! Resource string not found: [{}]", resKey, mre);
			resString = JMeterUtils.RES_KEY_PFX + key + "]";
		}
		return resString;
	}

}