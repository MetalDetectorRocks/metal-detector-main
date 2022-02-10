package rocks.metaldetector.web.controller.mvc.authentication;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import rocks.metaldetector.config.constants.ViewNames;

import static rocks.metaldetector.support.Endpoints.Authentication.LOGIN;

@Controller
@RequestMapping(LOGIN)
@Profile("!preview")
public class LoginController {

  @GetMapping
  public ModelAndView showLoginForm() {
    return new ModelAndView(ViewNames.Authentication.LOGIN);
  }
}
