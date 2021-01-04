package com.qdt.blockchain.support.utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qdt.blockchain.fx.controller.WorkAreaController;

public class EncryptDecryptUtil {
	private final static Logger LOGGER = LoggerFactory.getLogger(WorkAreaController.class);
	private final static String SALT = "ATY83D97FmAXaVdU";
	
	public static byte[] applyAES256Encryption(byte[] input, String encryptionPassword) {
		byte[] cipherBytes = null;
		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
	        KeySpec spec = new PBEKeySpec(encryptionPassword.toCharArray(), SALT.getBytes(), 65536, 128);
	        SecretKey tmpKey = factory.generateSecret(spec);
	        SecretKeySpec secretKey = new SecretKeySpec(tmpKey.getEncoded(), "AES");
	        IvParameterSpec iv = new IvParameterSpec(encryptionPassword.getBytes());
	        
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
			cipherBytes = cipher.doFinal(input);
		} catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException | InvalidAlgorithmParameterException e) {
			LOGGER.error(ExceptionUtils.getFullStackTrace(e));
		}
		return cipherBytes;
	}

	public static byte[] applyAES256Decryption(byte[] encryptedText, String decryptionPassword) {
		byte[] plainBytes = null;
		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
	        KeySpec spec = new PBEKeySpec(decryptionPassword.toCharArray(), SALT.getBytes(), 65536, 128);
	        SecretKey tmpKey = factory.generateSecret(spec);
	        SecretKeySpec secretKey = new SecretKeySpec(tmpKey.getEncoded(), "AES");
	        IvParameterSpec iv = new IvParameterSpec(decryptionPassword.getBytes());
	        
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
			plainBytes = cipher.doFinal(encryptedText);
		} catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException | InvalidAlgorithmParameterException e) {
			LOGGER.error(ExceptionUtils.getFullStackTrace(e));
		}
		return plainBytes;
	}
}
