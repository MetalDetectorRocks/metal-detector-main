package rocks.metaldetector.service.email;

import rocks.metaldetector.config.constants.ViewNames;

public final class AccountDeletedEmail extends AbstractEmail {

  public static final String SUBJECT = "Account deleted";

  private final String recipient;
  private final String username;

  public AccountDeletedEmail(String recipient, String username) {
    this.recipient = recipient;
    this.username = username;
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
    return ViewNames.EmailTemplates.ACCOUNT_DELETED;
  }

  @Override
  void buildViewModel() {
    addViewModelEntry(ViewModelEntry.builder()
                          .name("username")
                          .value(username)
                          .build());
  }
}
