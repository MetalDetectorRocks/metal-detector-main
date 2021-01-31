package rocks.metaldetector.web.controller.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.support.Endpoints;

@Controller
public class GuestLegalController {

  @GetMapping({Endpoints.Guest.IMPRINT})
  public ModelAndView showImprint() {
    return new ModelAndView(ViewNames.Guest.IMPRINT);
  }

  @GetMapping({Endpoints.Guest.PRIVACY_POLICY})
  public ModelAndView showPrivacyPolicy() {
    return new ModelAndView(ViewNames.Guest.PRIVACY_POLICY);
  }
}
