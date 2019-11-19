package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.model.exceptions.ErrorMessages;
import com.metalr2.model.exceptions.ValidationException;
import com.metalr2.model.user.UserEntity;
import com.metalr2.service.followArtist.FollowArtistService;
import com.metalr2.web.controller.discogs.ArtistSearchRestClient;
import com.metalr2.web.dto.FollowArtistDto;
import com.metalr2.web.dto.discogs.search.DiscogsArtistSearchResultContainer;
import com.metalr2.web.dto.discogs.search.DiscogsPagination;
import com.metalr2.web.dto.discogs.search.DiscogsPaginationUrls;
import com.metalr2.web.dto.request.ArtistSearchRequest;
import com.metalr2.web.dto.response.ArtistNameSearchResponse;
import com.metalr2.web.dto.response.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
               produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<ArtistNameSearchResponse> handleSearchRequest(@Valid @RequestBody ArtistSearchRequest artistSearchRequest, BindingResult bindingResult, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
    validateRequest(bindingResult);

    ArtistNameSearchResponse artistNameSearchResponse = searchArtist(artistSearchRequest, ((UserEntity)usernamePasswordAuthenticationToken.getPrincipal()).getPublicId());
    return ResponseEntity.ok(artistNameSearchResponse);
  }

  private void validateRequest(BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new ValidationException(ErrorMessages.VALIDATION_ERROR.toDisplayString(), bindingResult.getFieldErrors());
    }
  }

  private ArtistNameSearchResponse searchArtist(ArtistSearchRequest artistSearchRequest, String publicUserId) {
    Optional<DiscogsArtistSearchResultContainer> artistSearchResultsOptional = artistSearchRestClient.searchByName(artistSearchRequest.getArtistName(),
            artistSearchRequest.getPage(), artistSearchRequest.getSize());

    if (artistSearchResultsOptional.isEmpty()) {
      return new ArtistNameSearchResponse(Collections.EMPTY_LIST,new Pagination(),artistSearchRequest.getArtistName());
    }

    DiscogsArtistSearchResultContainer discogsArtistSearchResults = artistSearchResultsOptional.get();

    return createArtistNameSearchResponse(artistSearchRequest,discogsArtistSearchResults, publicUserId);
  }

  private ArtistNameSearchResponse createArtistNameSearchResponse(ArtistSearchRequest artistSearchRequest, DiscogsArtistSearchResultContainer artistSearchResults, String publicUserId) {
    DiscogsPagination discogsPagination         = artistSearchResults.getDiscogsPagination();
    DiscogsPaginationUrls discogsPaginationUrls = discogsPagination.getUrls();

    int size      = discogsPaginationUrls.getNext() != null ? discogsPagination.getItemsPerPage() : DEFAULT_PAGE_SIZE;
    int nextPage  = discogsPaginationUrls.getNext() != null ? discogsPagination.getCurrentPage() + 1 : DEFAULT_PAGE;

    List<FollowArtistDto> alreadyFollowedArtists = followArtistService.findPerUser(publicUserId);

    List<ArtistNameSearchResponse.ArtistSearchResult> dtoArtistSearchResults = artistSearchResults.getResults().stream()
            .map(artistSearchResult -> new ArtistNameSearchResponse.ArtistSearchResult(artistSearchResult.getThumb(),
                    artistSearchResult.getId(), artistSearchResult.getTitle(),
                    alreadyFollowedArtists.contains(new FollowArtistDto(publicUserId, artistSearchResult.getTitle(), artistSearchResult.getId()))))
            .collect(Collectors.toList());

    Pagination pagination = new Pagination(discogsPagination.getPagesTotal(), discogsPagination.getCurrentPage(),
            size, nextPage);

    return new ArtistNameSearchResponse(dtoArtistSearchResults, pagination, artistSearchRequest.getArtistName());
  }
}
