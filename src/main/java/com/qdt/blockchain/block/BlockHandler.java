package com.qdt.blockchain.block;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qdt.blockchain.support.utils.EncryptDecryptUtil;

public class BlockHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(BlockHandler.class);
	
	public static Block readBlock(String absolutePath, String fileName) {
		Block block = null;
		try {
			FileInputStream fi = new FileInputStream(new File(absolutePath + fileName + ".ser"));
			ObjectInputStream oi = new ObjectInputStream(fi);
			block = (Block) oi.readObject();
			oi.close();
			fi.close();
		} catch (Exception e) {
			LOGGER.error(ExceptionUtils.getFullStackTrace(e));
		}
		return block;
	}

	public static boolean writeBlock(String absolutePath, Block block) {
		try {
			Files.createDirectories(Paths.get(absolutePath));
			FileOutputStream fout = new FileOutputStream(absolutePath + block.getHash() + ".ser");
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(block);
			oos.close();
			fout.close();
		} catch (Exception e) {
			LOGGER.error(ExceptionUtils.getFullStackTrace(e));
			return false;
		}
		return true;
	}
	
	public static boolean updateBlockNextHash(String absolutePath, String prevHash, String nextHash) {
		Block block = readBlock(absolutePath, prevHash);
		block.setNextHash(nextHash);
		return writeBlock(absolutePath, block);
	}
	
	public static void downloadFile(String path, Block block) {
		try {
			if (block.getData() != null) {
				byte[] data = block.getData();
				Files.createDirectories(Paths.get(path));
				FileOutputStream fout = new FileOutputStream(new File(path + block.getFileName()));
				IOUtils.write(EncryptDecryptUtil.applyAES256Decryption(data, "AN3RNBCMHY6JQ84U"), fout);
				fout.close();
			}
		} catch (IOException e) {
			LOGGER.error(ExceptionUtils.getFullStackTrace(e));
		}
	}
}
