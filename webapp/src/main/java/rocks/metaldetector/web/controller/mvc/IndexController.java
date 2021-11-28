package rocks.metaldetector.web.controller.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.support.Endpoints;

@Controller
public class IndexController {

  @GetMapping({Endpoints.Guest.INDEX, Endpoints.Guest.EMPTY_INDEX, Endpoints.Guest.SLASH_INDEX})
  public ModelAndView showIndex() {
    return new ModelAndView(ViewNames.Frontend.INDEX);
  }
}
