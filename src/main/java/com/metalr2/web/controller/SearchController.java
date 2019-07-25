package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.web.controller.discogs.demo.ArtistSearchRestClient;
import com.metalr2.web.dto.discogs.search.ArtistSearchResults;
import com.metalr2.web.dto.discogs.search.Pagination;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

  @PostMapping({Endpoints.Frontend.FOLLOW_ARTISTS})
  public ModelAndView handleSearchRequest(@ModelAttribute SearchRequest searchRequest) {
    log.info(searchRequest.getArtistName());

    Optional<ArtistSearchResults> artistSearchResultsOptional = artistSearchRestClient.searchForArtist(searchRequest.getArtistName(), DEFAULT_PAGE, DEFAULT_PAGE_SIZE);

    if (artistSearchResultsOptional.isEmpty()) {
      // TODO NilsD: 24.07.19 return "artist not found" instead
      return new ModelAndView(ViewNames.Frontend.FOLLOW_ARTISTS);
    }

    ArtistSearchResults artistSearchResults = artistSearchResultsOptional.get();

    Map<String, Object> viewModel = buildViewModel(artistSearchResults, searchRequest.getArtistName());

    return new ModelAndView(ViewNames.Frontend.FOLLOW_ARTISTS, viewModel);
  }

  @GetMapping({Endpoints.Frontend.FOLLOW_ARTISTS})
  public ModelAndView showSearch(@RequestParam(name = "size", defaultValue = DEFAULT_PAGE_SIZE) int size,
                                 @RequestParam(name = "page", defaultValue = DEFAULT_PAGE) int page,
                                 @RequestParam(name = "artistName", defaultValue = DEFAULT_ARTIST_NAME) String artistName) {
    if (artistName.isEmpty()) {
      return getDefaultModelAndView();
    }

    Optional<ArtistSearchResults> artistSearchResultsOptional = artistSearchRestClient.searchForArtist(artistName, String.valueOf(page), String.valueOf(size));

    if (artistSearchResultsOptional.isEmpty()) {
      // TODO NilsD: 24.07.19 return "artist not found" instead
      return getDefaultModelAndView();
    }

    ArtistSearchResults artistSearchResults = artistSearchResultsOptional.get();
    Map<String, Object> viewModel = buildViewModel(artistSearchResults, artistName);

    return new ModelAndView(ViewNames.Frontend.FOLLOW_ARTISTS, viewModel);
  }

  private Map<String,Object> buildViewModel(ArtistSearchResults artistSearchResults, String artistName) {
    Map<String, Object> viewModel = new HashMap<>();
    viewModel.put("artistName", artistName);
    viewModel.put("artistSearchResultList", artistSearchResults.getResults());

    Pagination pagination = artistSearchResults.getPagination();
    List<String> pageNumbers = IntStream.rangeClosed(1, pagination.getPagesTotal()).boxed().map(String::valueOf).collect(Collectors.toList());

    viewModel.put("totalPages", pagination.getPagesTotal());
    viewModel.put("currentPage", pagination.getCurrentPage());
    viewModel.put("pageNumbers", pageNumbers);

    PaginationUrls paginationUrls = pagination.getUrls();
    String nextSize = DEFAULT_PAGE_SIZE;
    String nextPage = DEFAULT_PAGE;

    if (paginationUrls.getNext() != null){
      nextSize = String.valueOf(pagination.getItemsPerPage());
      nextPage = String.valueOf(pagination.getCurrentPage()+1);
    }

    viewModel.put("nextSize", nextSize);
    viewModel.put("nextPage", nextPage);

    return viewModel;
  }

  private ModelAndView getDefaultModelAndView() {
    Map<String, Object> viewModel = new HashMap<>();
    viewModel.put("totalPages", "0");
    return new ModelAndView(ViewNames.Frontend.FOLLOW_ARTISTS, viewModel);
  }

}
