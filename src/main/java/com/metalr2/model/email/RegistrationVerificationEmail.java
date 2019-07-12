package com.metalr2.model.email;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;

import java.util.HashMap;
import java.util.Map;

public final class RegistrationVerificationEmail extends AbstractEmail {

  private final String        recipient;
  private final String        emailVerificationToken;

  private static final String  VERIFICATION_URL = Endpoints.REGISTRATION_VERIFICATION + "?token=%s";
  private static final String  SUBJECT          = "One last step to complete your registration!";

  public RegistrationVerificationEmail(String recipient, String emailVerificationToken) {
    this.recipient              = recipient;
    this.emailVerificationToken = emailVerificationToken;
  }

  @Override
  public String getRecipient() {
    return recipient;
  }

  @Override
  public String getSubject() {
    return SUBJECT;
  }

  @Override
  public Map<String, Object> getViewModel() {
    Map<String, Object> viewModel = new HashMap<>();
    viewModel.put("verificationURL", createVerificationURL());

    return viewModel;
  }

  @Override
  public String getTemplateName() {
    return ViewNames.EmailTemplates.REGISTRATION_VERIFICATION;
  }

  private String createVerificationURL() {
    return String.format(HOST + ":" + PORT + VERIFICATION_URL, emailVerificationToken);
  }
}
