package com.metalr2.web.controller.mvc;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(Endpoints.Frontend.ARTISTS)
public class ArtistsController {

  @GetMapping(path = Endpoints.Frontend.SEARCH)
  public ModelAndView showSearch() {
    return new ModelAndView(ViewNames.Frontend.SEARCH);
  }

  @GetMapping(path = "/{discogsId}")
  public ModelAndView showArtistDetails(@PathVariable String discogsId) {
    return new ModelAndView(ViewNames.Frontend.ARTIST_DETAILS);
  }

}
