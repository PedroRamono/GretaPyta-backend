package com.az.gretapyta.questionnaires.util;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

/*
 * Thread-safe Singleton.
 */
@Component
public class EncryptionUtility {

  /* Maybe to be read from Properties */
  public static final int SALT_LENGTH = 30;
  private static final Random random = new SecureRandom();
  private static final String characters = "!_-@0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  private static final int ITERATIONS = 10000;
  private static final int KEY_LENGTH = 256;
  private static final String HASH_ALGORITHM = "PBKDF2WithHmacSHA1";

  private EncryptionUtility() {
  }

  /* Method to generate the salt value. */
  public static String getSaltvalue(int length) {
    StringBuilder finalval = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      finalval.append(characters.charAt(random.nextInt(characters.length())));
    }
    return new String(finalval);
  }

  /* Method to encrypt the password using the original password and salt value. */
  public static String generateSecurePassword(String password, String salt) {
    byte[] securePassword = hash(password.toCharArray(), salt.getBytes());
    return Base64.getEncoder().encodeToString(securePassword);
  }

  /* Method to verify if both password matches or not */
  public static boolean verifyUserPassword( String providedPassword,
                                            String securedPassword,
                                            String salt ) {
    /* Generate New secure password with the same salt */
    String newSecurePassword = generateSecurePassword(providedPassword, salt);
    /* Check if two passwords are equal */
    return newSecurePassword.equalsIgnoreCase(securedPassword);
  }

  /* Method to generate the hash value */
  private static byte[] hash(char[] password, byte[] salt) {
    PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
    Arrays.fill(password, Character.MIN_VALUE);
    try {
      SecretKeyFactory skf = SecretKeyFactory.getInstance(HASH_ALGORITHM);
      return skf.generateSecret(spec).getEncoded();
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
    } finally {
      spec.clearPassword();
    }
  }

  /* Test */
  public static void main(String[] args) {
    /* Plain text Password. */
    String password = "@myNewPass-123!";

    /* generates the Salt value. It can be stored in a database. */
    String saltValue = getSaltvalue(SALT_LENGTH);

    /* generates an encrypted password. It can be stored in a database.*/
    String encryptedPassword = generateSecurePassword(password, saltValue);

    /* Print out plain text password, encrypted password and salt value. */
    System.out.println("Plain text password = " + password);
    System.out.println("Secure password = " + encryptedPassword);
    System.out.println("Salt value = " + saltValue);

    /* verify the original password and encrypted password */
    boolean matched = verifyUserPassword(password, encryptedPassword, saltValue);
    System.out.println(matched ? "Password Matched !" : "Password Mismatched");
  }
}