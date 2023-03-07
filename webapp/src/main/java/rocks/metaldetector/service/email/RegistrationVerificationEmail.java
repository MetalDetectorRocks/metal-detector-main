package rocks.metaldetector.service.email;

import java.util.List;

import static rocks.metaldetector.service.email.EmailTemplateNames.REGISTRATION_VERIFICATION;
import static rocks.metaldetector.support.Endpoints.Frontend.SIGN_IN;

public final class RegistrationVerificationEmail implements Email {

  private static final String SUBJECT = "One last step to complete your registration!";

  private final String recipient;
  private final String username;
  private final String emailVerificationToken;

  public RegistrationVerificationEmail(String recipient, String username, String emailVerificationToken) {
    this.recipient = recipient;
    this.username = username;
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
  public String getTemplateName() {
    return REGISTRATION_VERIFICATION;
  }

  @Override
  public List<ViewModelEntry> getViewModelEntries() {
    return List.of(
        new ViewModelEntry("username", username),
        new ViewModelEntry("verificationUrl", SIGN_IN + "?token=" + emailVerificationToken, true)
    );
  }
}
