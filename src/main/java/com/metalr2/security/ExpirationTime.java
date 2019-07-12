package com.metalr2.security;

public enum ExpirationTime {

  ONE_SECOND(1000),
  ONE_HOUR(1000 * 60 * 60),
  TEN_DAYS(1000 * 60 * 60 * 24 * 10);

  private final long expirationTimeInMs;

  ExpirationTime(long expirationTimeInMs) {
    this.expirationTimeInMs = expirationTimeInMs;
  }

  public long toMillis() {
    return expirationTimeInMs;
  }

}
