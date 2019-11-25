package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.model.exceptions.ErrorMessages;
import com.metalr2.model.exceptions.ValidationException;
import com.metalr2.model.user.UserEntity;
import com.metalr2.service.followArtist.FollowArtistService;
import com.metalr2.web.controller.discogs.DiscogsArtistSearchRestClient;
import com.metalr2.web.dto.FollowArtistDto;
import com.metalr2.web.dto.discogs.search.DiscogsArtistSearchResultContainer;
import com.metalr2.web.dto.discogs.search.DiscogsPagination;
import com.metalr2.web.dto.request.ArtistSearchRequest;
import com.metalr2.web.dto.response.ArtistNameSearchResponse;
import com.metalr2.web.dto.response.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.metalr2.web.dto.response.ArtistNameSearchResponse.ArtistSearchResult;

@RestController
@RequestMapping(Endpoints.Rest.ARTISTS_V1)
public class SearchArtistRestController {

  private final DiscogsArtistSearchRestClient artistSearchClient;
  private final FollowArtistService followArtistService;

  @Autowired
  public SearchArtistRestController(DiscogsArtistSearchRestClient artistSearchClient, FollowArtistService followArtistService) {
    this.artistSearchClient = artistSearchClient;
    this.followArtistService = followArtistService;
  }

  @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
               produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<ArtistNameSearchResponse> handleSearchRequest(@Valid @RequestBody ArtistSearchRequest artistSearchRequest,
                                                                      BindingResult bindingResult, Authentication authentication) {
    validateRequest(bindingResult);

    Optional<ArtistNameSearchResponse> artistNameSearchResponse = searchArtist(artistSearchRequest, ((UserEntity)authentication.getPrincipal()).getPublicId());
    return ResponseEntity.of(artistNameSearchResponse);
  }

  private void validateRequest(BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new ValidationException(ErrorMessages.VALIDATION_ERROR.toDisplayString(), bindingResult.getFieldErrors());
    }
  }

  private Optional<ArtistNameSearchResponse> searchArtist(ArtistSearchRequest artistSearchRequest, String publicUserId) {
    Optional<DiscogsArtistSearchResultContainer> artistSearchResultsOptional = artistSearchClient.searchByName(artistSearchRequest.getArtistName(),
            artistSearchRequest.getPage(), artistSearchRequest.getSize());

    return artistSearchResultsOptional.isEmpty() ? Optional.empty() : createArtistNameSearchResponse(artistSearchResultsOptional.get(), publicUserId);
  }

  private Optional<ArtistNameSearchResponse> createArtistNameSearchResponse(DiscogsArtistSearchResultContainer artistSearchResults, String publicUserId) {
    DiscogsPagination discogsPagination = artistSearchResults.getDiscogsPagination();

    int size = discogsPagination.getItemsPerPage();

    Set<Long> alreadyFollowedArtists = followArtistService.findPerUser(publicUserId).stream().map(FollowArtistDto::getArtistDiscogsId)
           .collect(Collectors.toSet());

    List<ArtistSearchResult> dtoArtistSearchResults = artistSearchResults.getResults().stream()
            .map(artistSearchResult -> new ArtistSearchResult(artistSearchResult.getThumb(),artistSearchResult.getId(),
                    artistSearchResult.getTitle(),alreadyFollowedArtists.contains(artistSearchResult.getId())))
            .collect(Collectors.toList());

    Pagination pagination = new Pagination(discogsPagination.getPagesTotal(), discogsPagination.getCurrentPage(),
            size);

    return Optional.of(new ArtistNameSearchResponse(dtoArtistSearchResults, pagination));
  }
}
