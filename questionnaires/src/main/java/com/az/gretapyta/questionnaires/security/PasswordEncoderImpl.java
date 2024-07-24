package com.az.gretapyta.questionnaires.security;

import com.az.gretapyta.questionnaires.util.EncryptionUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class PasswordEncoderImpl implements PasswordEncoder {
  private final String salt;

  @Override // Implementing PasswordEncoder Interface
  public String encode(CharSequence rawPassword) {
    String rawPasswordAsString = String.valueOf(rawPassword);
    return EncryptionUtility.generateSecurePassword(rawPasswordAsString, salt);
  }

  @Override // Implementing PasswordEncoder Interface
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    String rawPasswordAsString = String.valueOf(rawPassword);
    return EncryptionUtility.verifyUserPassword(rawPasswordAsString, encodedPassword, salt);
  }
}