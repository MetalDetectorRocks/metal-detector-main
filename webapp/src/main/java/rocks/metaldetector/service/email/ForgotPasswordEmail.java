package rocks.metaldetector.service.email;

import static rocks.metaldetector.config.constants.ViewNames.EmailTemplates.FORGOT_PASSWORD;
import static rocks.metaldetector.support.Endpoints.Authentication.RESET_PASSWORD;

public final class ForgotPasswordEmail extends AbstractEmail {

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
  void buildViewModel() {
    addViewModelEntry(ViewModelEntry.builder()
            .name("username")
            .value(username)
            .build());

    addViewModelEntry(ViewModelEntry.builder()
            .name("resetPasswordUrl")
            .value(RESET_PASSWORD + "?token=" + resetPasswordToken)
            .relativeUrl(true)
            .build());
  }

}
