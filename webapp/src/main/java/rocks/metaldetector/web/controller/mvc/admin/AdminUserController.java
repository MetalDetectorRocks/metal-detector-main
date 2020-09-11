package rocks.metaldetector.web.controller.mvc.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.support.Endpoints;

@Controller
@RequestMapping(Endpoints.AdminArea.USERS)
public class AdminUserController {

  @GetMapping
  public ModelAndView showUser() {
    return new ModelAndView(ViewNames.AdminArea.USERS);
  }
}
