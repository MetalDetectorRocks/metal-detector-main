package rocks.metaldetector.web.controller.mvc.authentication;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.support.Endpoints;

@Controller
@AllArgsConstructor
@Profile("preview")
public class PreviewRegistrationController {

  @GetMapping(Endpoints.Guest.REGISTER)
  public ModelAndView showRegistrationForm() {
    return new ModelAndView(ViewNames.Authentication.DISABLED_REGISTER);
  }
}
