package com.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * utility class to read and store property (key, value) files for use by the application
 */
public class AppProperties {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(AppProperties.class);
	
	/**
	 * properties file name
	 */
	public static final String DEFAULT_FILE_NAME = "application.properties";

	/**
	 * map of the application's properties
	 */
	private Map<String, String> properties = new HashMap<String, String>();

	/**
	 * properties object 
	 */
	private Properties prop = new Properties();
	
	/**
	 * input stream to read properties file
	 */
	private InputStream input = null;
	
	/**
	 * No arg constructor that uses the default name for the properties file
	 */
	public AppProperties(){
		this(DEFAULT_FILE_NAME);
	}

	/**
	 * More generic constructor which takes in any file name
	 * @param fileName the file name with extension included where the properties are located
	 */
	public AppProperties(String fileName) {
		try {

			input = getClass().getClassLoader().getResourceAsStream(fileName);
			if (input == null) {
				System.out.println("Sorry, unable to find " + fileName);
				return;
			}

			prop.load(input);

			Enumeration<?> e = prop.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String value = prop.getProperty(key);
				LOGGER.info("Key : " + key + ", Value : " + value);
				properties.put(key, value);
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * @return map of properties
	 */
	public Map<String, String> getProperties() {
		return properties;
	}

}
