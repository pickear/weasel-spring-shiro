package com.weasel.security.helper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.weasel.core.helper.DemonPredict;


/**
 * @author Dylan
 * @time 2013-8-8
 */
public class PasswordEncoder {

	/**
	 * @param password
	 * @return
	 */
	public static String encode(String password) {
		DemonPredict.notEmpty(password);
		try {
			MessageDigest md5 = MessageDigest.getInstance("md5");
			char[] charArray = password.toCharArray();
			byte[] byteArray = new byte[charArray.length];
			for (int i = 0; i < charArray.length; i++)
				byteArray[i] = (byte) charArray[i];
			byte[] md5Bytes = md5.digest(byteArray);
			StringBuffer hexValue = new StringBuffer();

			for (int i = 0; i < md5Bytes.length; i++) {
				int val = ((int) md5Bytes[i]) & 0xff;
				if (val < 16)
					hexValue.append("0");
				hexValue.append(Integer.toHexString(val));
			}
			return hexValue.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

}
