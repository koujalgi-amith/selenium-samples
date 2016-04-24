package com.amithkoujalgi.selenium.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

import com.amithkoujalgi.selenium.utils.JSONUtils;

public class Configuration {
	public static boolean inited = false;

	public static class ConfigKey {
		public static final String CREDENTIALS = "credentials";
		public static final String QUERIES = "search_queries";
	}

	private static Configuration instance;
	private static Properties properties;

	private Configuration() {
	}

	public static Configuration getInstance() throws Exception {
		if (instance == null) {
			instance = new Configuration();
		}
		if (!inited) {
			init();
		}
		return instance;
	}

	private static void init() throws IOException {
		InputStream stream = Configuration.class.getClassLoader().getResourceAsStream("config.properties");
		properties = new Properties();
		properties.load(stream);
		System.out.println("Configuration found.");
		HashMap<Object, Object> map = new HashMap<>();
		for (Entry<Object, Object> e : properties.entrySet()) {
			map.put(e.getKey(), e.getValue());
		}
		JSONUtils.print(map);
		inited = true;
	}

	public Properties getProperties() {
		return properties;
	}

	public String getProperty(String key) {
		return properties.getProperty(key);
	}

}
