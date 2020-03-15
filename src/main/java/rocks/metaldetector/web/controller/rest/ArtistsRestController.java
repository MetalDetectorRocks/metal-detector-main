package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.service.artist.ArtistsService;
import rocks.metaldetector.web.dto.NameSearchResultsDto;
import rocks.metaldetector.web.dto.response.DiscogsNameSearchResponse;
import rocks.metaldetector.web.dto.response.Pagination;

import java.util.Optional;

@RestController
@RequestMapping(Endpoints.Rest.ARTISTS)
@AllArgsConstructor
public class ArtistsRestController {

  private final ArtistsService artistsService;

  @GetMapping(path = Endpoints.Rest.SEARCH,
              produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<DiscogsNameSearchResponse> handleNameSearch(@RequestParam(value = "query", defaultValue = "") String query,
                                                                    @PageableDefault Pageable pageable) {
    Optional<NameSearchResultsDto> resultOptional = artistsService.searchDiscogsByName(query, pageable);
    return ResponseEntity.of(resultOptional.map(nameSearchResultsDto -> mapResponse(nameSearchResultsDto, pageable)));
  }

  @PostMapping(path = Endpoints.Rest.FOLLOW + "/{discogsId}")
  public ResponseEntity<Void> handleFollow(@PathVariable long discogsId) {
    boolean success = artistsService.followArtist(discogsId);
    return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
  }

  @PostMapping(path = Endpoints.Rest.UNFOLLOW + "/{discogsId}")
  public ResponseEntity<Void> handleUnfollow(@PathVariable long discogsId) {
    boolean success = artistsService.unfollowArtist(discogsId);
    return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
  }

  private DiscogsNameSearchResponse mapResponse(NameSearchResultsDto dto, Pageable pageable) {
    DiscogsNameSearchResponse response = new DiscogsNameSearchResponse();
    response.setSearchResults(dto.getSearchResults());
    response.setPagination(new Pagination(dto.getResultCount(), pageable.getPageNumber(), pageable.getPageSize()));
    return response;
  }
}
