package rocks.metaldetector.service.email;

import rocks.metaldetector.model.email.AbstractEmail;

public interface EmailService {

  void sendEmail(AbstractEmail email);

}
