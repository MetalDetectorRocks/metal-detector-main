package rocks.metaldetector.service.email;

import java.util.List;

import static rocks.metaldetector.service.email.EmailTemplateNames.FORGOT_PASSWORD;
import static rocks.metaldetector.support.Endpoints.Authentication.RESET_PASSWORD;

public final class ForgotPasswordEmail implements Email {

  private static final String  SUBJECT = "Your password reset request";

  private final String recipient;
  private final String username;
  private final String resetPasswordToken;

  public ForgotPasswordEmail(String recipient, String username, String resetPasswordToken) {
    this.recipient          = recipient;
    this.username           = username;
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
  public String getTemplateName() {
    return FORGOT_PASSWORD;
  }

  @Override
  public List<ViewModelEntry> getViewModelEntries() {
    return List.of(
        new ViewModelEntry("username", username),
        new ViewModelEntry("resetPasswordUrl", RESET_PASSWORD + "?token=" + resetPasswordToken, true)
    );
  }
}
