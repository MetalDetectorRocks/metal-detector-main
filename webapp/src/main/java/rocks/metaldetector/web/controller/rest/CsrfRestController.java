package rocks.metaldetector.web.controller.rest;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static rocks.metaldetector.support.Endpoints.Rest.CSRF;

@RestController
public class CsrfRestController {

  @GetMapping(path = CSRF)
  public CsrfToken csrfToken(CsrfToken token) {
    return token;
  }
}
