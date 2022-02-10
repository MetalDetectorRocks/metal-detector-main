package rocks.metaldetector.web.controller.mvc;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import rocks.metaldetector.support.Endpoints;

import static rocks.metaldetector.config.constants.ViewNames.Authentication.PREVIEW_INDEX;

@Controller
@Profile("preview")
public class PreviewIndexController {

  @GetMapping({Endpoints.Frontend.INDEX, Endpoints.Frontend.HOME})
  public ModelAndView showPreviewIndex() {
    return new ModelAndView(PREVIEW_INDEX);
  }
}
