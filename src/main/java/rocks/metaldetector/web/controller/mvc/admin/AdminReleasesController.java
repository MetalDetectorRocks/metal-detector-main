package rocks.metaldetector.web.controller.mvc.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.config.constants.ViewNames;

@Controller
@RequestMapping(Endpoints.AdminArea.RELEASES)
public class AdminReleasesController {

  @GetMapping
  public ModelAndView showReleases() {
    return new ModelAndView(ViewNames.AdminArea.RELEASES);
  }
}