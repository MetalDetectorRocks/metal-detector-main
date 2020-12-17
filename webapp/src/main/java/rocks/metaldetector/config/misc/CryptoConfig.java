package rocks.metaldetector.config.misc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

@Configuration
public class CryptoConfig {

  static final String MD5_HASHING_ALGORITHM = "MD5";

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public Random secureRandom() {
    return new SecureRandom();
  }

  @Bean
  public MessageDigest messageDigest() {
    try {
      return MessageDigest.getInstance(MD5_HASHING_ALGORITHM);
    }
    catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Algorithm not found", e);
    }
  }
}
