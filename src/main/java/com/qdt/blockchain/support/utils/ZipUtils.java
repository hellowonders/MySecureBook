package com.qdt.blockchain.support.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipUtils {
	private final static Logger LOGGER = LoggerFactory.getLogger(ZipUtils.class);
	
	public static void pack(String sourceDirPath, String zipFilePath) throws IOException {
		File file = new File(zipFilePath);
        if (file.exists()){
            file.delete();
        }
	    Path p = Files.createFile(Paths.get(zipFilePath));
	    Path pp = Paths.get(sourceDirPath);
	    try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p));
	        Stream<Path> paths = Files.walk(pp)) {
	        paths
	          .filter(path -> !Files.isDirectory(path))
	          .forEach(path -> {
	              ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
	              try {
	                  zs.putNextEntry(zipEntry);
	                  Files.copy(path, zs);
	                  zs.closeEntry();
	            } catch (IOException e) {
	            	LOGGER.error(ExceptionUtils.getFullStackTrace(e));
	            }
	          });
	    }
	}
	
	public static void unpack(String outputDirPath, String zipFilePath) throws IOException {
		Files.createDirectories(Paths.get(outputDirPath));
		
	    FileInputStream fis = new FileInputStream(zipFilePath);
	    ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
	    ZipEntry entry;

	    while ((entry = zis.getNextEntry()) != null) {
	      int size;
	      byte[] buffer = new byte[2048];

	      FileOutputStream fos = new FileOutputStream(outputDirPath + entry.getName());
	      BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length);

	      while ((size = zis.read(buffer, 0, buffer.length)) != -1) {
	        bos.write(buffer, 0, size);
	      }
	      bos.flush();
	      bos.close();
	    }
	    zis.close();
	    fis.close();
	  }
}