package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.security.CurrentUserSupplier;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.support.Pagination;
import rocks.metaldetector.web.api.response.MyArtistsResponse;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(Endpoints.Rest.MY_ARTISTS)
@AllArgsConstructor
public class MyArtistsRestController {

  private final CurrentUserSupplier currentUserSupplier;

  @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<MyArtistsResponse> getMyArtists(@RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "size", defaultValue = "10") int size) {
    List<ArtistDto> followedArtists = mapFollowedArtists(currentUserSupplier.get().getFollowedArtists());

    Pageable pageable = PageRequest.of(page, size);
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), followedArtists.size());
    Page<ArtistDto> pages = new PageImpl<>(followedArtists.subList(start, end), pageable, followedArtists.size());

    Pagination pagination = new Pagination(followedArtists.size(), page, size);
    MyArtistsResponse response = new MyArtistsResponse(pages.toList(), pagination);
    return ResponseEntity.ok(response);
  }

  private List<ArtistDto> mapFollowedArtists(Set<ArtistEntity> followedArtistEntities) {
    return followedArtistEntities.stream().map(this::mapToArtistDto).collect(Collectors.toUnmodifiableList());
  }

  private ArtistDto mapToArtistDto(ArtistEntity artistEntity) {
    return ArtistDto.builder()
        .discogsId(artistEntity.getArtistDiscogsId())
        .artistName(artistEntity.getArtistName())
        .thumb(artistEntity.getThumb())
        .build();
  }
}
