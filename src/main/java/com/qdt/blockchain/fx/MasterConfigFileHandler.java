package com.qdt.blockchain.fx;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qdt.blockchain.support.utils.LicenseUtil;

public class MasterConfigFileHandler {
	final private static String PROPERTIES_FILE_PATH = System.getProperty("user.home")+"\\MySecureBook\\master.properties";
	private static final Logger LOGGER = LoggerFactory.getLogger(MasterConfigFileHandler.class.getName());
	
	private static MasterConfigFileHandler configFileHandler = new MasterConfigFileHandler();
	private final Properties configProp = new Properties();

	private MasterConfigFileHandler() {
		try {
			InputStream in = new FileInputStream(PROPERTIES_FILE_PATH);
			configProp.load(in);
		} catch (Exception e) {
			LOGGER.error(ExceptionUtils.getFullStackTrace(e));
		}
	}
	public static MasterConfigFileHandler getInstance() {
		return configFileHandler;
	}

	public boolean checkLicense() throws IOException, NoSuchAlgorithmException {
		String key = configProp.getProperty("LICENSE_KEY");
		String userID = configProp.getProperty("USER_ID");
		if (null == key || key.isEmpty()) {
			return false;
		} else {
			return LicenseUtil.validateSerial(userID, key);
		}
	}
	public void setLicense(String user, String license) throws IOException, NoSuchAlgorithmException {
		configProp.setProperty("USER_ID", user);
		configProp.setProperty("LICENSE_KEY", license);
		configProp.store(new FileOutputStream(PROPERTIES_FILE_PATH), null);
	}
	
	public String getUserId() {
		return configProp.getProperty("USER_DIR");
	}
	
	public String getLicenseKey() {
		return configProp.getProperty("LICENSE_KEY");
	}
	
	public String getWokspaceDir() {
		String userDir = configProp.getProperty("USER_DIR");
		String workspaceDir = System.getProperty(userDir);
		return workspaceDir + "\\MySecureBook\\";
	}
}
