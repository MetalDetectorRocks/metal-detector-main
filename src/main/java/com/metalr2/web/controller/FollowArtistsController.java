package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.web.controller.discogs.demo.ArtistSearchRestClient;
import com.metalr2.web.dto.discogs.search.ArtistSearchResultContainer;
import com.metalr2.web.controller.discogs.ArtistSearchRestClient;
import com.metalr2.web.dto.discogs.search.ArtistSearchResults;
import com.metalr2.web.dto.discogs.search.Pagination;
import com.metalr2.web.dto.discogs.search.PaginationUrls;
import com.metalr2.web.dto.request.ArtistSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@Slf4j
public class FollowArtistsController {

  private final ArtistSearchRestClient artistSearchRestClient;
  private static final String DEFAULT_PAGE_SIZE = "25";
  private static final String DEFAULT_PAGE = "1";
  private static final String DEFAULT_ARTIST_NAME = "";

  @Autowired
  public FollowArtistsController(ArtistSearchRestClient artistSearchRestClient) {
    this.artistSearchRestClient = artistSearchRestClient;
  }

  @ModelAttribute
  private ArtistSearchRequest searchRequest() {
    return new ArtistSearchRequest();
  }

  @PostMapping({Endpoints.Frontend.FOLLOW_ARTISTS})
  public ModelAndView handleSearchRequest(@ModelAttribute ArtistSearchRequest artistSearchRequest) {
    return createArtistSearchResultModelAndView(artistSearchRequest.getArtistName(), Integer.parseInt(DEFAULT_PAGE), Integer.parseInt(DEFAULT_PAGE_SIZE));
  }

  @GetMapping({Endpoints.Frontend.FOLLOW_ARTISTS})
  public ModelAndView showFollowArtists(@RequestParam(name = "size", defaultValue = DEFAULT_PAGE_SIZE) int size,
                                        @RequestParam(name = "page", defaultValue = DEFAULT_PAGE) int page,
                                        @RequestParam(name = "artistName", defaultValue = DEFAULT_ARTIST_NAME) String artistName) {
    if (artistName.equals(DEFAULT_ARTIST_NAME) && size == Integer.parseInt(DEFAULT_PAGE_SIZE)
                                               && page == Integer.parseInt(DEFAULT_PAGE)){
      return createDefaultModelAndView();
    }

    return createArtistSearchResultModelAndView(artistName, page, size);
  }

  private Map<String, Object> buildViewModel(ArtistSearchResultContainer artistSearchResults, String artistName) {
    Map<String, Object> viewModel = new HashMap<>();
    viewModel.put("artistName", artistName);
    viewModel.put("artistSearchResultList", artistSearchResults.getResults());

    Pagination pagination = artistSearchResults.getPagination();
    List<Integer> pageNumbers = IntStream.rangeClosed(1, pagination.getPagesTotal()).boxed().collect(Collectors.toList());

    viewModel.put("totalPages", pagination.getPagesTotal());
    viewModel.put("currentPage", pagination.getCurrentPage());
    viewModel.put("pageNumbers", pageNumbers);

    PaginationUrls paginationUrls = pagination.getUrls();
    var nextSize = paginationUrls.getNext() != null ? pagination.getItemsPerPage() : DEFAULT_PAGE_SIZE;
    var nextPage = paginationUrls.getNext() != null ? pagination.getCurrentPage() + 1 : DEFAULT_PAGE;

    viewModel.put("nextSize", nextSize);
    viewModel.put("nextPage", nextPage);

    return viewModel;
  }

  private ModelAndView createDefaultModelAndView() {
    Map<String, Object> viewModel = new HashMap<>();
    viewModel.put("totalPages", "0");
    return new ModelAndView(ViewNames.Frontend.FOLLOW_ARTISTS, viewModel);
  }

  private ModelAndView createArtistSearchResultModelAndView(String artistName, int page, int size) {
    Optional<ArtistSearchResultContainer> artistSearchResultsOptional = artistSearchRestClient.searchForArtistByName(artistName, page, size);

    if (artistSearchResultsOptional.isEmpty()) {
      return createBadArtistSearchRequestModelAndView(artistName, page, size);
    }

    ArtistSearchResultContainer artistSearchResults = artistSearchResultsOptional.get();

    Map<String, Object> viewModel = buildViewModel(artistSearchResults, artistName);
    return new ModelAndView(ViewNames.Frontend.FOLLOW_ARTISTS, viewModel);
  }

  private ModelAndView createBadArtistSearchRequestModelAndView(String artistName, int page, int size) {
    Map<String, Object> viewModel = new HashMap<>();
    viewModel.put("totalPages", "0");
    viewModel.put("badArtistSearchRequestMessage", "No data could be found for the given parameters:");
    viewModel.put("badArtistSearchRequestArtistName", artistName);
    viewModel.put("badArtistSearchRequestPage", page);
    viewModel.put("badArtistSearchRequestSize", size);
    return new ModelAndView(ViewNames.Frontend.FOLLOW_ARTISTS, viewModel);
  }

}
