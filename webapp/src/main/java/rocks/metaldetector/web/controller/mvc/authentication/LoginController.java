package rocks.metaldetector.web.controller.mvc.authentication;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.config.constants.ViewNames;

@Controller
@RequestMapping(Endpoints.Guest.LOGIN)
public class LoginController {

  @GetMapping
  public ModelAndView showLoginForm() {
    return new ModelAndView(ViewNames.Guest.LOGIN);
  }
}
