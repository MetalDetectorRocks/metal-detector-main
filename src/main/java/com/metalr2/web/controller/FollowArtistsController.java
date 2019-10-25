package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.service.followArtist.FollowArtistService;
import com.metalr2.web.controller.discogs.ArtistSearchRestClient;
import com.metalr2.web.dto.discogs.search.ArtistSearchResultContainer;
import com.metalr2.web.dto.discogs.search.Pagination;
import com.metalr2.web.dto.discogs.search.PaginationUrls;
import com.metalr2.web.dto.request.ArtistSearchRequest;
import com.metalr2.web.dto.request.FollowArtistRequest;
import com.metalr2.web.dto.response.ArtistNameSearchResponse;
import com.metalr2.web.dto.response.BadArtistNameSearchResponse;
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
public class FollowArtistsController {

  private final ArtistSearchRestClient artistSearchRestClient;
  private final FollowArtistService followArtistService;
  private static final String DEFAULT_PAGE_SIZE = "25";
  private static final String DEFAULT_PAGE = "1";
  private static final String DEFAULT_ARTIST_NAME = "";

  @Autowired
  public FollowArtistsController(ArtistSearchRestClient artistSearchRestClient, FollowArtistService followArtistService) {
    this.artistSearchRestClient = artistSearchRestClient;
    this.followArtistService = followArtistService;
  }

  @ModelAttribute
  private ArtistSearchRequest searchRequest() {
    return new ArtistSearchRequest();
  }

  @PostMapping({Endpoints.Frontend.FOLLOW_ARTISTS})
  public ModelAndView handleSearchRequest(@ModelAttribute ArtistSearchRequest artistSearchRequest) {
    return createArtistSearchResultModelAndView(artistSearchRequest.getArtistName(), Integer.parseInt(DEFAULT_PAGE), Integer.parseInt(DEFAULT_PAGE_SIZE));
  }

//  @GetMapping({Endpoints.Frontend.FOLLOW_ARTISTS})
//  public ModelAndView showFollowArtists(@RequestParam(name = "size", defaultValue = DEFAULT_PAGE_SIZE) int size,
//                                        @RequestParam(name = "page", defaultValue = DEFAULT_PAGE) int page,
//                                        @RequestParam(name = "artistName", defaultValue = DEFAULT_ARTIST_NAME) String artistName) {
//    if (artistName.equals(DEFAULT_ARTIST_NAME) && size == Integer.parseInt(DEFAULT_PAGE_SIZE)
//                                               && page == Integer.parseInt(DEFAULT_PAGE)){
//      return createDefaultModelAndView();
//    }
//
//    return createArtistSearchResultModelAndView(artistName, page, size);
//  }

  @GetMapping({Endpoints.Frontend.FOLLOW_ARTISTS})
  public ModelAndView followArtist(FollowArtistRequest followArtistRequest,
                                   @RequestParam(name = "size", defaultValue = DEFAULT_PAGE_SIZE) int size,
                                   @RequestParam(name = "page", defaultValue = DEFAULT_PAGE) int page,
                                   @RequestParam(name = "artistName", defaultValue = DEFAULT_ARTIST_NAME) String artistName) {
    if (followArtistRequest == null || (artistName.equals(DEFAULT_ARTIST_NAME) && size == Integer.parseInt(DEFAULT_PAGE_SIZE)
            && page == Integer.parseInt(DEFAULT_PAGE))){
      return createDefaultModelAndView();
    }

    followArtistService.followArtist(followArtistRequest);

    return createArtistSearchResultModelAndView(artistName, page, size);
  }

  private ArtistNameSearchResponse createArtistNameSearchResponse(ArtistSearchResultContainer artistSearchResults) {
    Pagination pagination = artistSearchResults.getPagination();
    List<Integer> pageNumbers = IntStream.rangeClosed(1, pagination.getPagesTotal()).boxed().collect(Collectors.toList());

    PaginationUrls paginationUrls = pagination.getUrls();
    int nextSize = paginationUrls.getNext() != null ? pagination.getItemsPerPage() : Integer.parseInt(DEFAULT_PAGE_SIZE);
    int nextPage = paginationUrls.getNext() != null ? pagination.getCurrentPage() + 1 : Integer.parseInt(DEFAULT_PAGE);

    List<ArtistNameSearchResponse.ArtistSearchResult> dtoArtistSearchResults = artistSearchResults.getResults().stream()
            .map(artistSearchResult -> new ArtistNameSearchResponse.ArtistSearchResult(artistSearchResult.getThumb(),
                    artistSearchResult.getId(), artistSearchResult.getTitle())).collect(Collectors.toList());
    ArtistNameSearchResponse.Pagination dtoPagination = new ArtistNameSearchResponse.Pagination(pagination.getPagesTotal(),
            pagination.getCurrentPage(), nextSize, nextPage, pageNumbers);

    return new ArtistNameSearchResponse(dtoArtistSearchResults, dtoPagination);
  }

  private ModelAndView createDefaultModelAndView() {
    return new ModelAndView(ViewNames.Frontend.FOLLOW_ARTISTS, "artistNameSearchResponse", new ArtistNameSearchResponse());
  }

  private ModelAndView createArtistSearchResultModelAndView(String artistName, int page, int size) {
    Optional<ArtistSearchResultContainer> artistSearchResultsOptional = artistSearchRestClient.searchByName(artistName, page, size);

    if (artistSearchResultsOptional.isEmpty()) {
      return createBadArtistSearchRequestModelAndView(artistName, page, size);
    }

    ArtistSearchResultContainer artistSearchResults = artistSearchResultsOptional.get();
    ArtistNameSearchResponse artistNameSearchResponse = createArtistNameSearchResponse(artistSearchResults);

    Map<String, Object> viewModel = new HashMap<>();
    viewModel.put("artistName", artistName);
    viewModel.put("artistNameSearchResponse", artistNameSearchResponse);

    return new ModelAndView(ViewNames.Frontend.FOLLOW_ARTISTS, viewModel);
  }

  private ModelAndView createBadArtistSearchRequestModelAndView(String artistName, int page, int size) {
    BadArtistNameSearchResponse badArtistNameSearchResponse = new BadArtistNameSearchResponse(page, size);
    Map<String, Object> viewModel = new HashMap<>();
    viewModel.put("artistName", artistName);
    viewModel.put("badArtistNameSearchResponse", badArtistNameSearchResponse);
    return new ModelAndView(ViewNames.Frontend.FOLLOW_ARTISTS, viewModel);
  }
}
