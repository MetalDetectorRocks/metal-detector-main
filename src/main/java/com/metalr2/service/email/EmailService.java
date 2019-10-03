package com.metalr2.service.email;

import com.metalr2.model.email.AbstractEmail;

public interface EmailService {

  void sendEmail(AbstractEmail email);

}
