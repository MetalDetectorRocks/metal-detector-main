package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {

  @Value("${spring.application.name}")
  private String applicationName;

  @GetMapping({Endpoints.INDEX, Endpoints.EMPTY_INDEX, Endpoints.SLASH_INDEX})
  public ModelAndView showIndex() {
    return new ModelAndView(ViewNames.INDEX, "applicationName", applicationName);
  }

}
