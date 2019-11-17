package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.model.exceptions.ErrorMessages;
import com.metalr2.model.exceptions.ValidationException;
import com.metalr2.service.followArtist.FollowArtistService;
import com.metalr2.web.controller.discogs.ArtistSearchRestClient;
import com.metalr2.web.dto.FollowArtistDto;
import com.metalr2.web.dto.discogs.search.ArtistSearchResultContainer;
import com.metalr2.web.dto.discogs.search.Pagination;
import com.metalr2.web.dto.discogs.search.PaginationUrls;
import com.metalr2.web.dto.request.ArtistSearchRequest;
import com.metalr2.web.dto.response.ArtistNameSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping({Endpoints.Rest.ARTISTS})
public class SearchArtistRestController {

  private static final int DEFAULT_PAGE_SIZE = 25;
  private static final int DEFAULT_PAGE = 1;

  private final ArtistSearchRestClient artistSearchRestClient;
  private final FollowArtistService followArtistService;

  @Autowired
  public SearchArtistRestController(ArtistSearchRestClient artistSearchRestClient,FollowArtistService followArtistService) {
    this.artistSearchRestClient = artistSearchRestClient;
    this.followArtistService    = followArtistService;
  }

  @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
               produces = MediaType.TEXT_HTML_VALUE)
  public ModelAndView handleSearchRequest(@Valid @RequestBody ArtistSearchRequest artistSearchRequest, BindingResult bindingResult) {
    validateRequest(bindingResult);

    return createArtistSearchResultModelAndView(artistSearchRequest,bindingResult);
  }

  private void validateRequest(BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new ValidationException(ErrorMessages.VALIDATION_ERROR.toDisplayString(), bindingResult.getFieldErrors());
    }
  }

  private ModelAndView createArtistSearchResultModelAndView(ArtistSearchRequest artistSearchRequest, BindingResult bindingResult) {
    Optional<ArtistSearchResultContainer> artistSearchResultsOptional = artistSearchRestClient.searchByName(artistSearchRequest.getArtistName(),
            artistSearchRequest.getPage(), artistSearchRequest.getSize());

    if (artistSearchResultsOptional.isEmpty()) {
      return createNoResultsModelAndView(artistSearchRequest.getArtistName());
    }

    ArtistSearchResultContainer artistSearchResults = artistSearchResultsOptional.get();
    ArtistNameSearchResponse artistNameSearchResponse = createArtistNameSearchResponse(artistSearchRequest,artistSearchResults);

    Map<String, Object> viewModel = new HashMap<>();
    viewModel.put("artistName", artistSearchRequest.getArtistName());
    viewModel.put("artistNameSearchResponse", artistNameSearchResponse);

    return new ModelAndView(ViewNames.Frontend.ARTIST_SEARCH_RESULTS, viewModel);
  }

  private ArtistNameSearchResponse createArtistNameSearchResponse(ArtistSearchRequest artistSearchRequest,ArtistSearchResultContainer artistSearchResults) {
    Pagination pagination = artistSearchResults.getPagination();
    List<Integer> pageNumbers = IntStream.rangeClosed(1, pagination.getPagesTotal()).boxed().collect(Collectors.toList());

    PaginationUrls paginationUrls = pagination.getUrls();
    int nextSize = paginationUrls.getNext() != null ? pagination.getItemsPerPage() : DEFAULT_PAGE_SIZE;
    int nextPage = paginationUrls.getNext() != null ? pagination.getCurrentPage() + 1 : DEFAULT_PAGE;

    List<FollowArtistDto> alreadyFollowedArtists = followArtistService.findPerUser(artistSearchRequest.getPublicUserId());

    List<ArtistNameSearchResponse.ArtistSearchResult> dtoArtistSearchResults = artistSearchResults.getResults().stream()
            .map(artistSearchResult -> new ArtistNameSearchResponse.ArtistSearchResult(artistSearchResult.getThumb(),
                    artistSearchResult.getId(), artistSearchResult.getTitle(),
                    alreadyFollowedArtists.contains(new FollowArtistDto(artistSearchRequest.getPublicUserId(), artistSearchResult.getTitle(), artistSearchResult.getId()))))
            .collect(Collectors.toList());
    ArtistNameSearchResponse.Pagination dtoPagination = new ArtistNameSearchResponse.Pagination(pagination.getPagesTotal(),
            pagination.getCurrentPage(), nextSize, nextPage, pageNumbers);

    return new ArtistNameSearchResponse(dtoArtistSearchResults, dtoPagination);
  }

  private ModelAndView createNoResultsModelAndView(String artistName) {
    return new ModelAndView(ViewNames.Frontend.ARTIST_SEARCH_RESULTS, "artistName", artistName);
  }
}
