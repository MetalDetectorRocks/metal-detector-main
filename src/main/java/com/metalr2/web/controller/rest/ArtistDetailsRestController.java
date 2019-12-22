package com.metalr2.web.controller.rest;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.security.CurrentUserSupplier;
import com.metalr2.service.artist.FollowArtistService;
import com.metalr2.service.discogs.DiscogsArtistSearchRestClient;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(Endpoints.Rest.ARTIST_DETAILS_V1)
public class ArtistDetailsRestController implements Validatable {

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

  @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<ArtistDetailsResponse> handleSearchRequest(@Valid ArtistDetailsRequest artistDetailsRequest, BindingResult bindingResult) {
    validateRequest(bindingResult);

    Optional<DiscogsArtist> artistOptional = artistSearchClient.searchById(artistDetailsRequest.getArtistId());

    if (artistOptional.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    ArtistDetailsResponse response = mapSearchResult(artistOptional.get());
    return ResponseEntity.ok(response);
  }

  private ArtistDetailsResponse mapSearchResult(DiscogsArtist discogsArtist) {
    String artistProfile      = discogsArtist.getProfile().isEmpty()      ? null : discogsArtist.getProfile();
    List<String> activeMember = discogsArtist.getDiscogsMembers() == null ? null : discogsArtist.getDiscogsMembers().stream().filter(DiscogsMember::isActive).map(DiscogsMember::getName).collect(Collectors.toList());
    List<String> formerMember = discogsArtist.getDiscogsMembers() == null ? null : discogsArtist.getDiscogsMembers().stream().filter(discogsMember -> !discogsMember.isActive()).map(DiscogsMember::getName).collect(Collectors.toList());
    List<String> images       = discogsArtist.getDiscogsImages()  == null ? null : discogsArtist.getDiscogsImages().stream().map(DiscogsImage::getResourceUrl).collect(Collectors.toList());
    boolean isFollowed        = followArtistService.exists(new FollowArtistDto(currentUserSupplier.get().getPublicId(), discogsArtist.getName(), discogsArtist.getId()));
    return new ArtistDetailsResponse(discogsArtist.getName(), discogsArtist.getId(), artistProfile, activeMember, formerMember, images, isFollowed);
  }

}
