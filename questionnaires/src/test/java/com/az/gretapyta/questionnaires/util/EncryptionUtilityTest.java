package com.az.gretapyta.questionnaires.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static com.az.gretapyta.questionnaires.util.EncryptionUtility.SALT_LENGTH;
import static org.junit.jupiter.api.Assertions.*;

public class EncryptionUtilityTest {

  @Test
  @Order(value = 1)
  @DisplayName("(1) When password encrypted and then decrypted with the same Salt, then they should match.")
  void test1() {
    String password = "@myNewPass-123!";
    String saltValue = EncryptionUtility.getSaltvalue(SALT_LENGTH);
    String encryptedPassword = EncryptionUtility.generateSecurePassword(password, saltValue);
    boolean matched = EncryptionUtility.verifyUserPassword(password, encryptedPassword, saltValue);

    assertNotNull(saltValue);
    assertNotNull(encryptedPassword);
    assertTrue(matched);
  }

  @Test
  @Order(value = 2)
  @DisplayName("(2) When password encrypted and decrypted, then should not match other password.")
  void test2() {
    String password = "@myNewPass-123!";
    String saltValue = EncryptionUtility.getSaltvalue(SALT_LENGTH);
    String encryptedPassword = EncryptionUtility.generateSecurePassword(password, saltValue);
    String otherPass = "A@myNewPass-123!";
    boolean matched = EncryptionUtility.verifyUserPassword(otherPass, encryptedPassword, saltValue);

    assertNotNull(saltValue);
    assertNotNull(encryptedPassword);
    assertFalse(matched);
  }

  @Test
  @Order(value = 3)
  @DisplayName("(3) When password encrypted with one Salt and decrypted with other Salt, then should not match.")
  void test3() {
    String password = "@myNewPass-123!";
    String saltValue = EncryptionUtility.getSaltvalue(SALT_LENGTH);
    String encryptedPassword = EncryptionUtility.generateSecurePassword(password, saltValue);
    String otherSaltValue = EncryptionUtility.getSaltvalue(SALT_LENGTH);
    boolean matched = EncryptionUtility.verifyUserPassword(password, encryptedPassword, otherSaltValue);

    assertNotNull(saltValue);
    assertNotNull(encryptedPassword);
    assertFalse(matched);
  }

  /*
  * Scenario with constant Salt: encrypting user password into Database on creation/update,
  * then on User login comparing encrypted logged pass. with encrypted DB-stored password.
  * User password should never be readable once encrypted into DB, only test available
  * if it matches.
  */
  @Test
  @Order(value = 4)
  @DisplayName("(4) When password encrypted with Salt and encrypted again with the same Salt, then encrypted results should match.")
  void test4() {
    String constantSaltValue = "TEuyxVYT2b7hAyBMRSTpkzDVLnc5!J"; // Constants.BASIC_SALT;
    String password1 = "@myNewPass-123!";

    String encryptedPassword1 = EncryptionUtility.generateSecurePassword(password1, constantSaltValue);
    String password2 = password1; // Like user login again.
    String encryptedPassword2 = EncryptionUtility.generateSecurePassword(password2, constantSaltValue);

    assertEquals(encryptedPassword1, encryptedPassword2);
  }
}