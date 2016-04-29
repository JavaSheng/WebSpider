package com.deepspider.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 计算字符串MD5值
 * @author JavaSheng
 */
public class CalcMD5 {
	public static String getMD5(String string) {
		
		char hexDigits[] = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
		MessageDigest msgDigest = null;
		try {
			msgDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		msgDigest.update(string.getBytes());
		byte[] updateBytes = msgDigest.digest();
		int len = updateBytes.length;
		char[] myChar = new char[len * 2];
		int k = 0;
		for(int i = 0; i < len; i++){
			byte byte0 = updateBytes[i];
			myChar[k++] = hexDigits[byte0 >>> 4 & 0x0f];
			myChar[k++] = hexDigits[byte0 & 0x0f];
		}
		
		return new String(myChar);
		
	}
}
