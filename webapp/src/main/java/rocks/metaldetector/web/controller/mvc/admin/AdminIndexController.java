package rocks.metaldetector.web.controller.mvc.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.support.Endpoints;

@Controller
public class AdminIndexController {

  @GetMapping({Endpoints.AdminArea.INDEX})
  public ModelAndView showUsersList() {
    return new ModelAndView(ViewNames.AdminArea.INDEX);
  }

}
