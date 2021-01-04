package com.qdt.blockchain.fx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InternalConfigFileHandler {
	private final static Logger LOGGER = LoggerFactory.getLogger(InternalConfigFileHandler.class);
	
	final private static String COMFIG_FILE_NAME = "config.properties";
	private String propFilePath;
	private Properties configProp;
	
	public String getPropFilePath() {
		return propFilePath;
	}
	public void setPropFilePath(String propFilePath) {
		this.propFilePath = propFilePath;
	}
	public InternalConfigFileHandler() {}
	public InternalConfigFileHandler(String propFilePath) {
		loadConfig(propFilePath);
	}
	
	public String getChainSize() {
		return configProp.getProperty("CHAIN_SIZE");
	}
	public String getFirstHash() {
		return configProp.getProperty("FIRST_HASH");
	}
	public String getLastHash() {
		return configProp.getProperty("LAST_HASH");
	}
	public void setProps(String firstHash, String lastHash) throws IOException, NoSuchAlgorithmException {
		if(firstHash != null && !firstHash.isEmpty())configProp.setProperty("FIRST_HASH", firstHash);
		if(lastHash != null && !lastHash.isEmpty())configProp.setProperty("LAST_HASH", lastHash);
		configProp.store(new FileOutputStream(getPropFilePath()), null);
	}
	
	public static void createConfigFile(String path) {
		try {
			path = MasterConfigFileHandler.getInstance().getWokspaceDir() + path;
			Files.createDirectories(Paths.get(path));
			File file = new File(path + "\\" + COMFIG_FILE_NAME);
			file.createNewFile();
		} catch (IOException e) {
			LOGGER.error(ExceptionUtils.getFullStackTrace(e));
		}
	}
	
	private void loadConfig(String propFilePath) {
		try {
			configProp = new Properties();
			String workspaceDir = MasterConfigFileHandler.getInstance().getWokspaceDir();
			String absolutePath = workspaceDir + propFilePath + COMFIG_FILE_NAME;
			setPropFilePath(absolutePath);
			InputStream in = new FileInputStream(absolutePath);
			configProp.load(in);
			
		} catch (Exception e) {
			LOGGER.error(ExceptionUtils.getFullStackTrace(e));
		}
	}
}
