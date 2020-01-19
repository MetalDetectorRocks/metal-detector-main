package com.metalr2.web.controller.mvc;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

  @GetMapping({Endpoints.Guest.INDEX, Endpoints.Guest.EMPTY_INDEX, Endpoints.Guest.SLASH_INDEX})
  public String showIndex(Authentication authentication) {
    if (authentication != null && authentication.isAuthenticated()) {
//      return new ModelAndView("redirect:" + ViewNames.Frontend.HOME);
      return "redirect:" + ViewNames.Frontend.HOME;
    }
//    return new ModelAndView(ViewNames.Guest.INDEX);
    return ViewNames.Guest.INDEX;
  }
}
