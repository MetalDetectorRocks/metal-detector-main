package com.metalr2.model.email;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;

import java.util.HashMap;
import java.util.Map;

public final class ForgotPasswordEmail extends AbstractEmail {

  private final String recipient;
  private final String userName;
  private final String resetPasswordToken;

  private static final String  RESET_PASSWORD_URL = Endpoints.Guest.RESET_PASSWORD + "?token=%s";
  private static final String  SUBJECT            = "Your password reset request";

  public ForgotPasswordEmail(String recipient, String userName, String resetPasswordToken) {
    this.recipient          = recipient;
    this.userName           = userName;
    this.resetPasswordToken = resetPasswordToken;
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
    viewModel.put("userName", userName);
    viewModel.put("resetPasswordURL", createResetPasswordURL());

    return viewModel;
  }

  @Override
  public String getTemplateName() {
    return ViewNames.EmailTemplates.FORGOT_PASSWORD;
  }

  private String createResetPasswordURL() {
    return String.format(HOST + ":" + PORT + RESET_PASSWORD_URL, resetPasswordToken);
  }
}
