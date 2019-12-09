package com.metalr2.web.controller.rest;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.security.CurrentUserSupplier;
import com.metalr2.service.artist.FollowArtistService;
import com.metalr2.service.discogs.DiscogsArtistSearchRestClient;
import com.metalr2.web.dto.FollowArtistDto;
import com.metalr2.web.dto.discogs.search.DiscogsArtistSearchResultContainer;
import com.metalr2.web.dto.discogs.search.DiscogsPagination;
import com.metalr2.web.dto.request.ArtistSearchRequest;
import com.metalr2.web.dto.response.ArtistNameSearchResponse;
import com.metalr2.web.dto.response.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
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
public class SearchArtistRestController implements Validatable {

  private final DiscogsArtistSearchRestClient artistSearchClient;
  private final FollowArtistService followArtistService;
  private final CurrentUserSupplier currentUserSupplier;

  @Autowired
  public SearchArtistRestController(DiscogsArtistSearchRestClient artistSearchClient, FollowArtistService followArtistService,
                                    CurrentUserSupplier currentUserSupplier) {
    this.artistSearchClient = artistSearchClient;
    this.followArtistService = followArtistService;
    this.currentUserSupplier = currentUserSupplier;
  }

  @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<ArtistNameSearchResponse> handleSearchRequest(@Valid ArtistSearchRequest artistSearchRequest,BindingResult bindingResult) {
    validateRequest(bindingResult);

    Optional<DiscogsArtistSearchResultContainer> artistSearchResultsOptional = artistSearchClient.searchByName(artistSearchRequest.getArtistName(),
            artistSearchRequest.getPage(), artistSearchRequest.getSize());

    if (artistSearchResultsOptional.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    ArtistNameSearchResponse response = mapSearchResult(artistSearchResultsOptional.get(), currentUserSupplier.get().getPublicId());
    return ResponseEntity.ok(response);
  }

  private ArtistNameSearchResponse mapSearchResult(DiscogsArtistSearchResultContainer artistSearchResults, String publicUserId) {
    DiscogsPagination discogsPagination = artistSearchResults.getDiscogsPagination();

    int itemsPerPage = discogsPagination.getItemsPerPage();

    Set<Long> alreadyFollowedArtists = followArtistService.findPerUser(publicUserId).stream().map(FollowArtistDto::getArtistDiscogsId)
           .collect(Collectors.toSet());

    List<ArtistSearchResult> dtoArtistSearchResults = artistSearchResults.getResults().stream()
            .map(artistSearchResult -> new ArtistSearchResult(artistSearchResult.getThumb(),artistSearchResult.getId(),
                    artistSearchResult.getTitle(), alreadyFollowedArtists.contains(artistSearchResult.getId())))
            .collect(Collectors.toList());

    Pagination pagination = new Pagination(discogsPagination.getPagesTotal(), discogsPagination.getCurrentPage(), itemsPerPage);

    return new ArtistNameSearchResponse(dtoArtistSearchResults, pagination);
  }

}
