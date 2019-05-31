package com.wk.main;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class MyLogger {

	private static String strLoggerPath = "log4j.properties";

	// private static String strLoggerPath = "PPFSlog4j.cfg";//ppfs

	public static void setLoggerPath(String strPath) {

		strLoggerPath = strPath;
	}

	/**
	 * public static Logger getMyLogger(Class<?> cls) {
	 * 
	 * Logger log = Logger.getLogger(cls);
	 * PropertyConfigurator.configureAndWatch(strLoggerPath, 5); return log; }
	 */

//	static Logger log = null;
	
	public static Logger log = null;

	static {
			log = Logger.getLogger(MyLogger.class);
			PropertyConfigurator.configureAndWatch(strLoggerPath, 5);
	}

	public static Logger getMyLogger(Class<?> cls) {

		if (null == log) {
			log = Logger.getLogger(MyLogger.class);
			PropertyConfigurator.configureAndWatch(strLoggerPath, 5);
		}

		return log;
	}

	public static Logger getMyLogger(Class<?> cls, String propertiesFilePath) {
		strLoggerPath = propertiesFilePath;
		if (null == log) {
			log = Logger.getLogger(MyLogger.class);
			PropertyConfigurator.configureAndWatch(strLoggerPath, 5);
		}

		return log;
	}

	public static void main(String[] args) {

		String msg = "aaa";
		Logger log = MyLogger.getMyLogger(String.class);
		Logger log2 = MyLogger.getMyLogger(String.class, "redis.properties");

		log.info(msg);
		log2.info(msg);
	}
}
