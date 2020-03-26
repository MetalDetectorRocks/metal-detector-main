package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.butler.facade.ReleaseService;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.service.artist.ArtistsService;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.web.api.request.DetectorReleasesRequest;
import rocks.metaldetector.web.api.response.DetectorReleasesResponse;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(Endpoints.Rest.RELEASES)
@AllArgsConstructor
public class ReleasesRestController {

  private final ReleaseService releaseService;
  private final ArtistsService artistsService;
  private final ModelMapper mapper;

  @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
               produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<List<DetectorReleasesResponse>> getReleases(@Valid @RequestBody DetectorReleasesRequest request) {
    List<ReleaseDto> releaseDtos = releaseService.findReleases(request.getArtists(), request.getDateFrom(), request.getDateTo());
    return ResponseEntity.ok(mapReleasesResponse(releaseDtos));
  }

  private List<DetectorReleasesResponse> mapReleasesResponse(List<ReleaseDto> releaseDtos) {
    List<String> followedArtistsNames = artistsService.findFollowedArtistsForCurrentUser().stream().map(ArtistDto::getArtistName).collect(Collectors.toList());
    return releaseDtos.stream().map(dto -> {
      DetectorReleasesResponse response = mapper.map(dto, DetectorReleasesResponse.class);
      response.setFollowed(followedArtistsNames.contains(dto.getArtist()));
      return response;
    }).collect(Collectors.toList());
  }
}
