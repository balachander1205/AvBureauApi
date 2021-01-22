package com.avanse.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptUtil{

	private static final String characterEncoding = "UTF-8";
	private static final String cipherTransformation = "AES/CBC/PKCS5Padding";	
	private static final String aesEncryptionAlgorithm = "AES";
	private static final String key = "A745F72A4230A1CBF4D93EA702687567";
	private static final String initialVector = "123456789ABCDEFG";



	public static String encryptString(String s, String secr) {
		try {
			byte[] plainTextbytes = s.getBytes(characterEncoding);
			byte[] keyBytes = key.getBytes(characterEncoding);
			byte[] ivBytes = initialVector.getBytes(characterEncoding);
			String encryptedString = Base64.getEncoder().encodeToString(encrypt(plainTextbytes, keyBytes, ivBytes));

			return encryptedString;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return s;
	}

	private static byte[] encrypt(byte[] plainText, byte[] key, byte[] initialVector) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(cipherTransformation);
		SecretKeySpec secretKeySpec = new SecretKeySpec(key, aesEncryptionAlgorithm);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
		plainText = cipher.doFinal(plainText);
		return plainText;
	}



	public static String decryptString(String encryptedText, String secr) {
		try {
			byte[] cipheredBytes = Base64.getDecoder().decode(encryptedText);
			byte[] keyBytes = key.getBytes(characterEncoding);
			byte[] ivBytes = initialVector.getBytes(characterEncoding);
			String decryptedString = new String(decrypt(cipheredBytes, keyBytes, ivBytes));

			return decryptedString;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return encryptedText;
	}

	private static byte[] decrypt(byte[] cipherText, byte[] key, byte[] initialVector) throws Exception {
		Cipher cipher = Cipher.getInstance(cipherTransformation);
		SecretKeySpec secretKeySpecy = new SecretKeySpec(key, aesEncryptionAlgorithm);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpecy, ivParameterSpec);
		cipherText = cipher.doFinal(cipherText);
		return cipherText;
	}

}
