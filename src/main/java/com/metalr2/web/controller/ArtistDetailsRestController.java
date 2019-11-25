package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.model.exceptions.ErrorMessages;
import com.metalr2.model.exceptions.ValidationException;
import com.metalr2.model.user.UserEntity;
import com.metalr2.service.followArtist.FollowArtistService;
import com.metalr2.web.controller.discogs.DiscogsArtistSearchRestClient;
import com.metalr2.web.dto.FollowArtistDto;
import com.metalr2.web.dto.discogs.artist.DiscogsArtist;
import com.metalr2.web.dto.discogs.artist.DiscogsMember;
import com.metalr2.web.dto.discogs.misc.DiscogsImage;
import com.metalr2.web.dto.request.ArtistDetailsRequest;
import com.metalr2.web.dto.response.ArtistDetailsResponse;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping(Endpoints.Rest.ARTIST_DETAILS_V1)
public class ArtistDetailsRestController {

  private final DiscogsArtistSearchRestClient artistSearchClient;
  private final FollowArtistService followArtistService;

  @Autowired
  public ArtistDetailsRestController(DiscogsArtistSearchRestClient artistSearchClient, FollowArtistService followArtistService) {
    this.artistSearchClient = artistSearchClient;
    this.followArtistService    = followArtistService;
  }

  @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
               produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<ArtistDetailsResponse> handleSearchRequest(@Valid @RequestBody ArtistDetailsRequest artistDetailsRequest, BindingResult bindingResult,
                                                                   Authentication authentication) {
    validateRequest(bindingResult);

    return ResponseEntity.of(searchArtistDetails(artistDetailsRequest, ((UserEntity)authentication.getPrincipal()).getPublicId()));
  }

  private void validateRequest(BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new ValidationException(ErrorMessages.VALIDATION_ERROR.toDisplayString(), bindingResult.getFieldErrors());
    }
  }

  private Optional<ArtistDetailsResponse> searchArtistDetails(ArtistDetailsRequest artistDetailsRequest, String publicUserId) {
    Optional<DiscogsArtist> artistOptional = artistSearchClient.searchById(artistDetailsRequest.getArtistId());

    return artistOptional.isEmpty() ? Optional.empty() : createArtistDetailsResponse(artistOptional.get(), artistDetailsRequest, publicUserId);
  }

  private Optional<ArtistDetailsResponse> createArtistDetailsResponse(DiscogsArtist discogsArtist, ArtistDetailsRequest artistDetailsRequest, String publicUserId) {
    String artistProfile      = discogsArtist.getProfile().isEmpty()      ? null : discogsArtist.getProfile();
    List<String> activeMember = discogsArtist.getDiscogsMembers() == null ? null : discogsArtist.getDiscogsMembers().stream().filter(DiscogsMember::isActive).map(DiscogsMember::getName).collect(Collectors.toList());
    List<String> formerMember = discogsArtist.getDiscogsMembers() == null ? null : discogsArtist.getDiscogsMembers().stream().filter(discogsMember -> !discogsMember.isActive()).map(DiscogsMember::getName).collect(Collectors.toList());
    List<String> images       = discogsArtist.getDiscogsImages()  == null ? null : discogsArtist.getDiscogsImages().stream().map(DiscogsImage::getResourceUrl).collect(Collectors.toList());
    boolean isFollowed        = followArtistService.exists(new FollowArtistDto(publicUserId, artistDetailsRequest.getArtistName(), discogsArtist.getId()));
    return Optional.of(new ArtistDetailsResponse(artistDetailsRequest.getArtistName(), artistDetailsRequest.getArtistId(), artistProfile, activeMember, formerMember, images, isFollowed));
  }
}
