package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.model.exceptions.ErrorMessages;
import com.metalr2.model.exceptions.ValidationException;
import com.metalr2.security.CurrentUserSupplier;
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
  private final CurrentUserSupplier currentUserSupplier;

  @Autowired
  public ArtistDetailsRestController(DiscogsArtistSearchRestClient artistSearchClient, FollowArtistService followArtistService,
                                     CurrentUserSupplier currentUserSupplier) {
    this.artistSearchClient = artistSearchClient;
    this.followArtistService = followArtistService;
    this.currentUserSupplier = currentUserSupplier;
  }

  @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
               produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<ArtistDetailsResponse> handleSearchRequest(@Valid @RequestBody ArtistDetailsRequest artistDetailsRequest,
                                                                   BindingResult bindingResult) {
    validateRequest(bindingResult);

    Optional<DiscogsArtist> artistOptional = artistSearchClient.searchById(artistDetailsRequest.getArtistId());

    if (artistOptional.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    ArtistDetailsResponse response = mapSearchResult(artistOptional.get(), artistDetailsRequest, currentUserSupplier.get().getPublicId());
    return ResponseEntity.ok(response);
  }

  private void validateRequest(BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new ValidationException(ErrorMessages.VALIDATION_ERROR.toDisplayString(), bindingResult.getFieldErrors());
    }
  }

  private ArtistDetailsResponse mapSearchResult(DiscogsArtist discogsArtist, ArtistDetailsRequest artistDetailsRequest, String publicUserId) {
    String artistProfile      = discogsArtist.getProfile().isEmpty()      ? null : discogsArtist.getProfile();
    List<String> activeMember = discogsArtist.getDiscogsMembers() == null ? null : discogsArtist.getDiscogsMembers().stream().filter(DiscogsMember::isActive).map(DiscogsMember::getName).collect(Collectors.toList());
    List<String> formerMember = discogsArtist.getDiscogsMembers() == null ? null : discogsArtist.getDiscogsMembers().stream().filter(discogsMember -> !discogsMember.isActive()).map(DiscogsMember::getName).collect(Collectors.toList());
    List<String> images       = discogsArtist.getDiscogsImages()  == null ? null : discogsArtist.getDiscogsImages().stream().map(DiscogsImage::getResourceUrl).collect(Collectors.toList());
    boolean isFollowed        = followArtistService.exists(new FollowArtistDto(publicUserId, artistDetailsRequest.getArtistName(), discogsArtist.getId()));
    return new ArtistDetailsResponse(artistDetailsRequest.getArtistName(), artistDetailsRequest.getArtistId(), artistProfile, activeMember, formerMember, images, isFollowed);
  }

}
