package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
 
/**
 * A utility class that encrypts or decrypts a file.
 * @author www.codejava.net
 *
 */
public class CryptoUtils {
	private static final String SECRET_KEY ="IsmailBouaddi997";
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    
    public static String encryptData(String strToEncrypt) throws Exception {
    	SecretKey secretKey = new SecretKeySpec(SECRET_KEY.getBytes(),0,SECRET_KEY.length(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte [] encryptedData = cipher.doFinal(strToEncrypt.getBytes());
        byte [] encodedEncryptedData = Base64.getEncoder().encode(encryptedData);
    	return new String(encodedEncryptedData);
    }
    public static String decryptedData(String encodedEncryptedData) throws Exception {
    	SecretKey secretKey = new SecretKeySpec(SECRET_KEY.getBytes(),0,SECRET_KEY.length(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte [] decodedencryptedData =Base64.getDecoder().decode(encodedEncryptedData);
        byte[] outputBytes = cipher.doFinal(decodedencryptedData);
    	return new String(outputBytes);
    }
    
    public static void main(String[] args) throws Exception {
    	String output = encryptData("ismail bouoaddi is the best");
		System.out.println(output);
		System.out.println(decryptedData(output));
		
		
		
	}
 
  
}
class CryptoException extends Exception {
	 
    public CryptoException() {
    }
 
    public CryptoException(String message, Throwable throwable) {
        super(message, throwable);
    }
}