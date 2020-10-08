package rocks.metaldetector.web.controller.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.support.Endpoints;

@Controller
@RequestMapping(Endpoints.Frontend.PROFILE)
public class ProfileController {

  @GetMapping
  public ModelAndView showProfile() {
    return new ModelAndView(ViewNames.Frontend.PROFILE);
  }

  @GetMapping(path = Endpoints.Frontend.SPOTIFY_CALLBACK)
  public ModelAndView showProfile(@RequestParam(value = "code") String code,
                                  @RequestParam(value = "state") String state) {
    return new ModelAndView(ViewNames.Frontend.PROFILE);
  }
}
