package com.metalr2.web.controller.mvc;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(Endpoints.Frontend.RELEASES)
public class ReleasesController {

  @GetMapping
  public ModelAndView showReleases() {
    return new ModelAndView(ViewNames.Frontend.RELEASES);
  }
}
