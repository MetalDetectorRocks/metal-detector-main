package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.web.controller.discogs.demo.ArtistSearchRestClient;
import com.metalr2.web.dto.request.SearchRequest;
import com.metalr2.web.dto.response.SearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class SearchController {

  private final ArtistSearchRestClient artistSearchRestClient;
  private final ModelMapper mapper;

  @Autowired
  public SearchController(ArtistSearchRestClient artistSearchRestClient) {
    this.artistSearchRestClient = artistSearchRestClient;
    this.mapper = new ModelMapper();
  }

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

    List<SearchResponse> searchResults = artistSearchRestClient.searchForArtist(searchRequest.getArtistName())
            .stream().map(result -> new SearchResponse(result.getId(),result.getTitle(),result.getResourceUrl()))
            .collect(Collectors.toList());

    Map<String,Object> map = new HashMap<>();
    map.put("searchRequest", new SearchRequest());
    map.put("artistName", searchRequest.getArtistName());
    map.put("searchResults", searchResults);

    return new ModelAndView(ViewNames.SEARCH, map);

  }

}
