package rocks.metaldetector.web.controller.mvc.authentication;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.config.constants.ViewNames;

@Controller
@AllArgsConstructor
@Profile("!default")
public class PreviewRegistrationController {

  @GetMapping(Endpoints.Guest.REGISTER)
  public ModelAndView showRegistrationForm() {
    return new ModelAndView(ViewNames.Guest.DISABLED_REGISTER);
  }
}
