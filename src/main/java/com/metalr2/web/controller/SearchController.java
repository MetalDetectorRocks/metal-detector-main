package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.web.dto.request.SearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
public class SearchController {

  @ModelAttribute
  private SearchRequest searchRequest(){
    return new SearchRequest();
  }

  @GetMapping({Endpoints.SEARCH})
  public ModelAndView showSearchForm() {
    return new ModelAndView(ViewNames.SEARCH);
  }

  @PostMapping(path = {Endpoints.SEARCH})
  public ModelAndView handleSearchRequest(@ModelAttribute SearchRequest searchRequest){

    log.info(searchRequest.getArtistName());

    Map<String,Object> map = new HashMap<>();
    map.put("searchRequest", new SearchRequest());
    map.put("artistName", searchRequest.getArtistName());

    return new ModelAndView(ViewNames.SEARCH, map);

  }

}
