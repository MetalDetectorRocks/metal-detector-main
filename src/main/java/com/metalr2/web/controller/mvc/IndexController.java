package com.metalr2.web.controller.mvc;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.service.redirection.RedirectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class IndexController {

  private final RedirectionService redirectionService;

  @Autowired
  public IndexController(RedirectionService redirectionService) {
    this.redirectionService = redirectionService;
  }

  @GetMapping({Endpoints.Guest.INDEX, Endpoints.Guest.EMPTY_INDEX, Endpoints.Guest.SLASH_INDEX})
  public ModelAndView showIndex(Authentication authentication) {
    Optional<ModelAndView> redirectionOptional = redirectionService.getRedirectionIfNeeded(authentication);
    return redirectionOptional.orElseGet(() -> new ModelAndView(ViewNames.Guest.INDEX));
  }
}
