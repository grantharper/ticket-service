package com.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppProperties {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(AppProperties.class);

	private Map<String, String> properties = new HashMap<String, String>();

	private Properties prop = new Properties();
	private InputStream input = null;

	public AppProperties() {
		try {

			String filename = "application.properties";
			input = getClass().getClassLoader().getResourceAsStream(filename);
			if (input == null) {
				System.out.println("Sorry, unable to find " + filename);
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

	public Map<String, String> getProperties() {
		return properties;
	}

}
