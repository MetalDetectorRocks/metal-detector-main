package rocks.metaldetector.web.controller.mvc;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import static rocks.metaldetector.config.constants.ViewNames.Authentication.PREVIEW_INDEX;
import static rocks.metaldetector.support.Endpoints.Frontend.HOME;
import static rocks.metaldetector.support.Endpoints.Frontend.INDEX;

@Controller
@Profile("preview")
public class PreviewIndexController {

  @GetMapping({INDEX, HOME})
  public ModelAndView showPreviewIndex() {
    return new ModelAndView(PREVIEW_INDEX);
  }
}
