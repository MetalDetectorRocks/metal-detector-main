package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.web.controller.discogs.demo.ArtistSearchRestClient;
import com.metalr2.web.dto.discogs.search.ArtistSearchResults;
import com.metalr2.web.dto.discogs.search.PaginationUrls;
import com.metalr2.web.dto.request.SearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@Slf4j
public class SearchController {

  private final ArtistSearchRestClient artistSearchRestClient;
  private static final String DEFAULT_PAGE_SIZE = "25";
  private static final String DEFAULT_PAGE = "1";
  private static final String DEFAULT_ARTIST_NAME = "";

  @Autowired
  public SearchController(ArtistSearchRestClient artistSearchRestClient) {
    this.artistSearchRestClient = artistSearchRestClient;
  }

  @ModelAttribute
  private SearchRequest searchRequest() {
    return new SearchRequest();
  }

  @PostMapping({Endpoints.SEARCH})
  public ModelAndView handleSearchRequest(@ModelAttribute SearchRequest searchRequest) {
    log.info(searchRequest.getArtistName());

    Optional<ArtistSearchResults> artistSearchResultsOptional = artistSearchRestClient.searchForArtist(searchRequest.getArtistName(), DEFAULT_PAGE, DEFAULT_PAGE_SIZE);

    if (artistSearchResultsOptional.isEmpty()) {
      return new ModelAndView(ViewNames.SEARCH);
    }

    ArtistSearchResults artistSearchResults = artistSearchResultsOptional.get();

    Map<String, Object> viewModel = new HashMap<>();
    viewModel.put("artistName", searchRequest.getArtistName());
    viewModel.put("artistSearchResultList", artistSearchResults.getResults());

    PaginationUrls paginationUrls = artistSearchResults.getPagination().getUrls();

    if (paginationUrls.getNext() != null){
      String size = this.getSizeFromNext(paginationUrls.getNext());
      String page = this.getPageFromNext(paginationUrls.getNext());
      viewModel.put("size", size);
      viewModel.put("page", page);
    }

    return new ModelAndView(ViewNames.SEARCH, viewModel);
  }

  @GetMapping({Endpoints.SEARCH})
  public ModelAndView showSearch(@RequestParam(name = "size", defaultValue = DEFAULT_PAGE_SIZE) int size,
                                 @RequestParam(name = "page", defaultValue = DEFAULT_PAGE) int page,
                                 @RequestParam(name = "artistName", defaultValue = DEFAULT_ARTIST_NAME) String artistName) {
    if (artistName.isEmpty()) {
      return new ModelAndView(ViewNames.SEARCH);
    }

    Optional<ArtistSearchResults> artistSearchResultsOptional = artistSearchRestClient.searchForArtist(artistName, String.valueOf(page), String.valueOf(size));

    if (artistSearchResultsOptional.isEmpty()) {
      return new ModelAndView(ViewNames.SEARCH);
    }

    ArtistSearchResults artistSearchResults = artistSearchResultsOptional.get();

    Map<String, Object> viewModel = new HashMap<>();
    viewModel.put("artistName", artistName);
    viewModel.put("artistSearchResultList", artistSearchResults.getResults());
    viewModel.put("size", size);
    viewModel.put("page", page);

    return new ModelAndView(ViewNames.SEARCH, viewModel);
  }

  private String getSizeFromNext(String nextUrl){
    return nextUrl.substring(nextUrl.indexOf("&per_page=")+10, nextUrl.indexOf("&type=artist&page="));
  }

  private String getPageFromNext(String nextUrl){
    return nextUrl.substring(nextUrl.indexOf("&type=artist&page=")+18);
  }

}
