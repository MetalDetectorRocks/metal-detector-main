package rocks.metaldetector.web.controller.mvc.authentication;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.support.Endpoints;

@Controller
@RequestMapping(Endpoints.Guest.LOGIN)
@Profile("!preview")
public class LoginController {

  @GetMapping
  public ModelAndView showLoginForm() {
    return new ModelAndView(ViewNames.Authentication.LOGIN);
  }
}
