package com.blazemeter.jmeter.citrix.utils;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Serialize/Deserialize objects in/from a given file
 */
public class XmlSerializationHelper {

	private XmlSerializationHelper() {
	}

	/**
	 * Serialize an object in a file
	 * 
	 * @param object to serialize
	 * @param file   the destination file
	 * @throws IOException when serialization error occurs
	 */
	public static void encodeToFile(Object object, File file) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(file);
		        XMLEncoder encoder = new XMLEncoder(fos)) {
			// object serialization
			encoder.writeObject(object);
			encoder.flush();
		}
	}

	/**
	 * Deserialize a file
	 * 
	 * @param file source file
	 * @param      <T> class of the object to deserialize
	 * @return the deserialized Object
	 * @throws IOException when deserialization error occurs
	 */
	@SuppressWarnings("unchecked")
	public static <T> T decodeFromFile(File file) throws IOException {
		Object object = null;
		try (FileInputStream fis = new FileInputStream(file); 
		        XMLDecoder decoder = new XMLDecoder(fis)) {
			object = decoder.readObject();
		}
		return (T) object;
	}

}
