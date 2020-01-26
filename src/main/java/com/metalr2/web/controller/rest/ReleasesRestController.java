package com.metalr2.web.controller.rest;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.model.exceptions.ErrorMessages;
import com.metalr2.model.exceptions.ValidationException;
import com.metalr2.service.artist.ArtistsService;
import com.metalr2.service.releases.ReleasesService;
import com.metalr2.web.dto.ArtistDto;
import com.metalr2.web.dto.releases.ButlerReleasesRequest;
import com.metalr2.web.dto.request.DetectorReleasesRequest;
import com.metalr2.web.dto.response.DetectorReleasesResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(Endpoints.Rest.RELEASES)
public class ReleasesRestController {

  private final ReleasesService releasesService;
  private final ArtistsService artistsService;
  private final ModelMapper mapper;

  @Autowired
  public ReleasesRestController(ReleasesService releasesService, ArtistsService artistsService, ModelMapper mapper) {
    this.releasesService = releasesService;
    this.artistsService = artistsService;
    this.mapper = mapper;
  }

  @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
               produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<List<DetectorReleasesResponse>> getReleases(@Valid @RequestBody DetectorReleasesRequest request, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new ValidationException(ErrorMessages.VALIDATION_ERROR.toDisplayString(), bindingResult.getFieldErrors());
    }

    List<DetectorReleasesResponse> releaseResponses = releasesService.getReleases(mapper.map(request, ButlerReleasesRequest.class)).stream()
        .map(releaseDto -> mapper.map(releaseDto, DetectorReleasesResponse.class))
        .collect(Collectors.toList());
    List<String> followedArtistsNames = artistsService.findFollowedArtistsForCurrentUser().stream().map(ArtistDto::getArtistName).collect(Collectors.toList());
    releaseResponses.forEach(releaseResponse -> releaseResponse.setIsFollowed(String.valueOf(followedArtistsNames.contains(releaseResponse.getArtist()))));
    return ResponseEntity.ok(releaseResponses);
  }
}
