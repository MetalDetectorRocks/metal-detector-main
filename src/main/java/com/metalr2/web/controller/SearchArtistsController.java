package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(Endpoints.Frontend.SEARCH_ARTISTS)
public class SearchArtistsController {

  @GetMapping
  public ModelAndView showSearchArtists() {
    return new ModelAndView(ViewNames.Frontend.SEARCH_ARTISTS);
  }
}
