package com.qdt.blockchain.support.utils;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class LicenseUtil {
	private static String DATE_FORMATTER_PATTERN = "ddMMyy";
	
	public static void main(String[] args) {/*
		try {
			System.out.println(generateLicense("manoj.nayak@6th-sense.in", 90));
//			System.out.println(validateSerial("123@gmail.com", "91280-0A619-C6291-34C40-1484658164"));
//			System.out.println(renewLicense("123@gmail.com", "91280-0A619-C6291-34C40-1484658164", "91290-0A419-C6291-34C40-1485579763"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	*/}
	
	public static String generateLicense(String input, Integer days) throws NoSuchAlgorithmException {
		String expiryDate = getExpiryDate(days);
		String finalSerial = getSerial(input, expiryDate.substring(0, 3), expiryDate.substring(3, 6));
		return (finalSerial + "-" + expiryDate.hashCode()).toUpperCase();
	}

	public static boolean validateSerial(String input, String license) throws NoSuchAlgorithmException {
		String finalSerial = getSerial(input, license.substring(2, 5), license.substring(8,11));
		
		String providedDate = license.substring(2, 5) + license.substring(8,11);
		DateTimeFormatter format = DateTimeFormatter.ofPattern(DATE_FORMATTER_PATTERN); 
		LocalDate localDateProvided = LocalDate.parse(providedDate, format);
		
		return license.equals((finalSerial+"-"+providedDate.hashCode()).toUpperCase()) && localDateProvided.isAfter(LocalDate.now());
	}

	public static String renewLicense(String input, String currentLicense, String newLicense) throws NoSuchAlgorithmException {
		
		LocalDate today = LocalDate.now();
		String providedDate = currentLicense.substring(2, 5) + currentLicense.substring(8,11);
		DateTimeFormatter format = DateTimeFormatter.ofPattern(DATE_FORMATTER_PATTERN); 
		LocalDate localDateProvided = LocalDate.parse(providedDate, format);
		long extraDays = ChronoUnit.DAYS.between(today, localDateProvided);
		
		String newDate = newLicense.substring(2, 5) + newLicense.substring(8,11);
		LocalDate localDateNew = LocalDate.parse(newDate, format);
		LocalDate localDateExtended = localDateNew.plusDays(extraDays);
		String extendedDate = localDateExtended.format(format);
		
		return (getSerial(input, extendedDate.substring(0, 3), extendedDate.substring(3, 6)) + "-" + extendedDate.hashCode()).toUpperCase();
	}
	
	public static long getLicenseRemainingDays(String license) throws NoSuchAlgorithmException {
		
		DateTimeFormatter format = DateTimeFormatter.ofPattern(DATE_FORMATTER_PATTERN); 
		String providedDate = license.substring(2, 5) + license.substring(8,11);
		LocalDate localDateProvided = LocalDate.parse(providedDate, format);
		return ChronoUnit.DAYS.between(LocalDate.now(), localDateProvided);
	}
	
	private static String calculateSecurityHash(String stringInput, String algorithmName)
			throws java.security.NoSuchAlgorithmException {
		String hexMessageEncode = "";
		byte[] buffer = stringInput.getBytes();
		java.security.MessageDigest messageDigest = java.security.MessageDigest.getInstance(algorithmName);
		messageDigest.update(buffer);
		byte[] messageDigestBytes = messageDigest.digest();
		for (int index = 0; index < messageDigestBytes.length; index++) {
			int countEncode = messageDigestBytes[index] & 0xff;
			if (Integer.toHexString(countEncode).length() == 1)
				hexMessageEncode = hexMessageEncode + "0";
			hexMessageEncode = hexMessageEncode + Integer.toHexString(countEncode);
		}
		return hexMessageEncode;
	}

	private static String getSerial(String input, String datePartFirst, String datePartSecond) throws NoSuchAlgorithmException {
		String serialNumberEncoded = calculateSecurityHash(input, "MD2") + calculateSecurityHash(input, "MD5")
		+ calculateSecurityHash(input, "SHA1");
		String serialNumber = "" + serialNumberEncoded.charAt(32) + serialNumberEncoded.charAt(76)
				+ datePartFirst + "-" + serialNumberEncoded.charAt(72)
				+ serialNumberEncoded.charAt(98) + datePartSecond + "-" + serialNumberEncoded.charAt(47) + serialNumberEncoded.charAt(65)
				+ serialNumberEncoded.charAt(18) + serialNumberEncoded.charAt(85) + serialNumberEncoded.charAt(50) + "-" + serialNumberEncoded.charAt(27)
				+ serialNumberEncoded.charAt(53) + serialNumberEncoded.charAt(102) + serialNumberEncoded.charAt(15)
				+ serialNumberEncoded.charAt(99);
		return serialNumber;
	}
	
	private static String getExpiryDate(Integer days) {
		LocalDate date = LocalDate.now().plusDays(days).plusDays(1);
		DateTimeFormatter format = DateTimeFormatter.ofPattern("ddMMyy"); 
		return date.format(format);
	}

}
