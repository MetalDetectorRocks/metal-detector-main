package com.metalr2.web.controller.mvc;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {

  @GetMapping({Endpoints.Guest.INDEX, Endpoints.Guest.EMPTY_INDEX, Endpoints.Guest.SLASH_INDEX})
  public ModelAndView showIndex() {
    return new ModelAndView(ViewNames.Guest.INDEX);
  }

}
