package rocks.metaldetector.service.email;

import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.config.constants.ViewNames;

public final class RegistrationVerificationEmail extends AbstractEmail {

  private static final String SUBJECT = "One last step to complete your registration!";

  private final String recipient;
  private final String emailVerificationToken;

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
  void buildViewModel() {
    addViewModelEntry(ViewModelEntry.builder()
            .name("verificationUrl")
            .value(Endpoints.Guest.REGISTRATION_VERIFICATION + "?token=" + emailVerificationToken)
            .relativeUrl(true)
            .build());
  }

  @Override
  public String getTemplateName() {
    return ViewNames.EmailTemplates.REGISTRATION_VERIFICATION;
  }

}
