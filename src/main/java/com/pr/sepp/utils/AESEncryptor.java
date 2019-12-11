package com.pr.sepp.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@Slf4j
public class AESEncryptor {

	public static String encrypt(String toEncrypted, String sKey) {
		try {
			if (sKey == null || sKey.length() != 16) {
				return null;
			}
			byte[] raw = sKey.getBytes();
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
			byte[] encrypted = cipher.doFinal(toEncrypted.getBytes());

			return Base64.encodeBase64String(encrypted);
		} catch (Exception ex) {
			log.error("用户名密码解密失败", ex);
			return null;
		}
	}

	public static String decrypt(String toDecrypted, String sKey) {
		try {
			if (sKey == null || sKey.length() != 16) {
				return null;
			}
			byte[] raw = sKey.getBytes("ASCII");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] encrypted1 = org.apache.commons.codec.binary.Base64.decodeBase64(toDecrypted);
			try {
				byte[] original = cipher.doFinal(encrypted1);
				String originalString = new String(original);
				return originalString;
			} catch (Exception e) {
				log.error("用户名密码解密失败", e);
				throw e;
			}
		} catch (Exception ex) {
			log.error("用户名密码解密失败", ex);
			throw new RuntimeException("用户名密码解密失败", ex);
		}
	}
}