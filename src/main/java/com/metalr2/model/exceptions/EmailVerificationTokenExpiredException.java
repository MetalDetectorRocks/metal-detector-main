package com.metalr2.model.exceptions;

public class EmailVerificationTokenExpiredException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public EmailVerificationTokenExpiredException() {
    super(ErrorMessages.EMAIL_VERIFICATION_TOKEN_EXPIRED.toDisplayString());
  }

}
