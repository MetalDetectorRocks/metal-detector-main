package com.metalr2.utils;

import java.security.SecureRandom;
import java.util.Random;

public class Utils {

  private static final Random RANDOM   = new SecureRandom();
  private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

  private Utils() {
    throw new UnsupportedOperationException("util class - no instance needed!");
  }

  public static String generatePublicId(int length) {
    return generateRandomString(length);
  }

  private static String generateRandomString(int length) {
    StringBuilder returnValue = new StringBuilder(length);

    for (int i = 0; i < length; i++) {
      returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
    }

    return returnValue.toString();
  }
	
}
