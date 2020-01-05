package com.metalr2.web.controller.mvc;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.web.dto.request.SearchRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(Endpoints.Frontend.ARTISTS)
public class ArtistsController {

  private static final String DEFAULT_PAGE = "1";
  private static final String DEFAULT_SIZE = "10";

  @GetMapping(path = "/search")
  public ModelAndView showSearch(@RequestParam(value = "query", defaultValue = "") String query,
                                 @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
                                 @RequestParam(value = "size", defaultValue = DEFAULT_SIZE) int size) {
    return new ModelAndView(ViewNames.Frontend.SEARCH, "searchRequest", new SearchRequest(query,page,size));
  }

  @GetMapping(path = "/{discogsId}")
  public ModelAndView showArtistDetails(@PathVariable long discogsId) {
    return new ModelAndView(ViewNames.Frontend.ARTIST_DETAILS, "discogsId", discogsId);
  }

}
