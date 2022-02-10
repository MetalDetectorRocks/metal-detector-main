package rocks.metaldetector.web.controller.mvc.authentication;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import static rocks.metaldetector.config.constants.ViewNames.Authentication.DISABLED_REGISTER;
import static rocks.metaldetector.support.Endpoints.Authentication.REGISTER;

@Controller
@AllArgsConstructor
@Profile("preview")
public class PreviewRegistrationController {

  @GetMapping(REGISTER)
  public ModelAndView showRegistrationForm() {
    return new ModelAndView(DISABLED_REGISTER);
  }
}
