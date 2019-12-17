package com.pr.sepp.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@Slf4j
public class AESEncryptor {

	private static final String ERROR_EN_MSG = "用户名密码解密失败";
	private static final String ERROR_DE_MSG = "用户名密码解密失败";
	private static final String CIPHER_INSTANCE = "AES/CBC/PKCS5Padding";
	private static final String SERIAL_KEY = "0102030405060708";
	private static final String ENCRYPT_STYLE = "AES";

	public static String encrypt(String toEncrypted, String sKey) {
		try {
			if (sKey == null || sKey.length() != 16) {
				return null;
			}
			byte[] raw = sKey.getBytes();
			SecretKeySpec skeySpec = new SecretKeySpec(raw, ENCRYPT_STYLE);
			Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE);
			IvParameterSpec iv = new IvParameterSpec(SERIAL_KEY.getBytes());
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
			byte[] encrypted = cipher.doFinal(toEncrypted.getBytes());

			return Base64.encodeBase64String(encrypted);
		} catch (Exception ex) {
			log.error(ERROR_EN_MSG, ex);
			return null;
		}
	}

	public static String decrypt(String toDecrypted, String sKey) {
		try {
			if (sKey == null || sKey.length() != 16) {
				return null;
			}
			byte[] raw = sKey.getBytes("ASCII");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, ENCRYPT_STYLE);
			Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE);
			IvParameterSpec iv = new IvParameterSpec(SERIAL_KEY.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] encrypted1 = org.apache.commons.codec.binary.Base64.decodeBase64(toDecrypted);
			try {
				return new String(cipher.doFinal(encrypted1));
			} catch (Exception e) {
				log.error(ERROR_DE_MSG, e);
				throw e;
			}
		} catch (Exception ex) {
			log.error(ERROR_DE_MSG, ex);
			throw new RuntimeException(ERROR_DE_MSG, ex);
		}
	}
}