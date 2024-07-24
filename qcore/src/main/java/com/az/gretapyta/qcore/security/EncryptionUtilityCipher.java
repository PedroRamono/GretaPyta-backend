package com.az.gretapyta.qcore.security;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

//TODO if needed in the future
public class EncryptionUtilityCipher {
  private static final String ALGORITHM = "AES";
  private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

  public String encryptMessage(byte[] message, byte[] keyBytes) throws InvalidKeyException, NoSuchPaddingException,
      NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
    Cipher cipher = Cipher.getInstance(TRANSFORMATION);
    SecretKey secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
    byte[] encryptedMessage = cipher.doFinal(message);
    return new String(encryptedMessage);
  }

  public String decryptMessage(byte[] encryptedMessage, byte[] keyBytes) throws NoSuchPaddingException,
      NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
    Cipher cipher = Cipher.getInstance(TRANSFORMATION);
    SecretKey secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE, secretKey);
    byte[] clearMessage = cipher.doFinal(encryptedMessage);
    return new String(clearMessage);
  }

  /*
  public static void main(String[] args) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException,
      BadPaddingException, IllegalBlockSizeException {
    String encKeyString = "1234567890123456";
    String message = "Java Guides";

    JavaCipherClassDemo cipherClassDemo = new JavaCipherClassDemo();
    String encryptedstr = encryptMessage(message.getBytes(), encKeyString.getBytes());

    String decryptedStr = cipherClassDemo.decryptMessage(encryptedstr.getBytes(), encKeyString.getBytes());
    System.out.println("Original String -> " + message);
    System.out.println("Encrypted String -> " + encryptedstr);
    System.out.println("Decrypted String -> " + decryptedStr);
  }
  */
}