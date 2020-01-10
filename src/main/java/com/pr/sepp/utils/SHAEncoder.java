package com.pr.sepp.utils;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.security.MessageDigest;

public class SHAEncoder {
	public static String encodeSHA(byte[] data) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA");
		byte[] digest = md.digest(data);
		return new HexBinaryAdapter().marshal(digest);
	}

	public static String encodeSHA256(byte[] data) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] digest = md.digest(data);
		return new HexBinaryAdapter().marshal(digest);
	}

	public static String encodeSHA384(byte[] data) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-384");
		byte[] digest = md.digest(data);
		return new HexBinaryAdapter().marshal(digest);
	}

	public static String encodeSHA512(byte[] data) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		byte[] digest = md.digest(data);
		return new HexBinaryAdapter().marshal(digest);
	}
}
