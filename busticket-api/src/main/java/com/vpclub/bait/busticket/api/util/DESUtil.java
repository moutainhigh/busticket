package com.vpclub.bait.busticket.api.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Base64;

public class DESUtil {

	private final static String CipherType = "DES/CBC/PKCS5Padding";
	private final static String CharSet = "UTF-8";

	// 解密数据
	public static String decrypt(String message, String key) throws Exception {

		byte[] bytesrc = convertHexString(message);
		Cipher cipher = Cipher.getInstance(CipherType);
		DESKeySpec desKeySpec = new DESKeySpec(key.getBytes(CharSet));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
		IvParameterSpec iv = new IvParameterSpec(key.getBytes(CharSet));

		cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

		byte[] retByte = cipher.doFinal(bytesrc);
		return new String(retByte,CharSet);
	}

	public static byte[] encrypt(String message, String key) throws Exception {
		Cipher cipher = Cipher.getInstance(CipherType);

		DESKeySpec desKeySpec = new DESKeySpec(key.getBytes(CharSet));

		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
		IvParameterSpec iv = new IvParameterSpec(key.getBytes(CharSet));
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

		return cipher.doFinal(message.getBytes(CharSet));
	}

	public static byte[] convertHexString(String ss) {
		byte digest[] = new byte[ss.length() / 2];
		for (int i = 0; i < digest.length; i++) {
			String byteString = ss.substring(2 * i, 2 * i + 2);
			int byteValue = Integer.parseInt(byteString, 16);
			digest[i] = (byte) byteValue;
		}

		return digest;
	}

	public static String toHexString(byte b[]) {     
        StringBuffer hexString = new StringBuffer();     
        for (int i = 0; i < b.length; i++) {     
            String plainText = Integer.toHexString(0xff & b[i]);     
            if (plainText.length() < 2)     
                plainText = "0" + plainText;     
            hexString.append(plainText);     
        }     
             
        return hexString.toString();     
    } 
	
	public static String Md5(String msg){
		  try {
		    MessageDigest md = MessageDigest.getInstance("md5");
		    byte md5[] = md.digest(msg.getBytes());
		   /*
		    BASE64Encoder encoder = new BASE64Encoder();
		    return encoder.encode(md5);//解释md5码成明文字符串
		  */
		    return toHexString(md5);
		  
		  } catch (Exception e) {
		    throw new RuntimeException(e);
		   }
	}

	/**
	 * 先base64编码后再des加密
	 * @param message   要加密的文本
	 * @param charSet   加密编码格式
	 * @param key       加密的key
	 * @return
	 * @throws Exception
	 */
	public static String base64AndDesEncode(String message,String charSet, String key) throws Exception{
		byte[] msgBytearray = Base64.getEncoder().encode(message.getBytes(charSet));
		byte[] encryptByteArray = encrypt(new String(msgBytearray,CharSet),key);
		return toHexString(encryptByteArray).toUpperCase();
	}
	
	/**
	 * 解密经过base64编码和des加密的密文
	 * @param miwen    要解密的密文
	 * @param charSet  加密时所用的编码格式
	 * @param key      加密时用到的key
	 * @return
	 * @throws Exception
	 */
	public static String base64AndDesDecode(String miwen,String charSet, String key) throws Exception{
		String decryptStr = decrypt(miwen,key);
		byte[] messageArray = Base64.getDecoder().decode(decryptStr);
		return new String(messageArray,charSet);
	}
	
	public static void main1(String[] args) throws UnsupportedEncodingException, Exception {
	
//        System.out.println(ma);
//		String md5str=Md5("MATICSOFT").toUpperCase();
//		System.out.println(md5str);
//		String key="*&^$%#(&";
//		//String key = "11111111";     
//        String jiami="1MZBNDU1V0h8MDJ8NzY5MQ==余聪";     
//        //String jiami=java.net.URLEncoder.encode(value, "GB2312").toLowerCase();     
//             
//        System.out.println("加密数据:"+jiami);     
//        String a=toHexString(encrypt(jiami, key)).toUpperCase();     
//
//        System.out.println("加密后的数据为:"+a);     
//        String b=java.net.URLDecoder.decode(decrypt(a,key), "utf-8") ;     
//        System.out.println("解密后的数据:"+b);  
		
		 String key = "*&^$%#(&";
////		 String key = "11111111";
//		 String message = "你好，我是测试报文！";
////		 String message = "{\"reqData\":{\"amount\":7520,\"paymentData\":{\"payAmount\":7520,\"customerName\":\"???\",\"billKey\":\"05026914\",\"filed1\":\"201508\",\"companyId\":\"871000201\"}},\"reqType\":{\"payType\":\"2\",\"payCode\":\"2\"}}";
//		 System.out.println("加密数据前:"+message);
////		 String message = "云A455WH|02|7691";
//		 System.out.println("密钥：" + key);
//		 String jiami = base64AndDesEncode(message,"UTF-8",key);
//		 System.out.println("加密数据:"+jiami);
		 String result = base64AndDesDecode("58BB5118E6FDD474302F01FA03A546E5","UTF-8",key);
		 System.out.println("解密后的数据:"+result); 
		
	}

}
