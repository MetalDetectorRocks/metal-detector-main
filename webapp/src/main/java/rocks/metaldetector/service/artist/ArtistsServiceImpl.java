package rocks.metaldetector.service.artist;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.discogs.facade.DiscogsService;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultDto;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.persistence.domain.artist.ArtistRepository;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.security.CurrentPublicUserIdSupplier;
import rocks.metaldetector.spotify.facade.SpotifyService;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistSearchResultDto;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;
import rocks.metaldetector.web.api.response.ArtistSearchResponse;
import rocks.metaldetector.web.transformer.ArtistSearchResponseTransformer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ArtistsServiceImpl implements ArtistsService {

  private final ArtistRepository artistRepository;
  private final UserRepository userRepository;
  private final DiscogsService discogsService;
  private final SpotifyService spotifyService;
  private final CurrentPublicUserIdSupplier currentPublicUserIdSupplier;
  private final ArtistTransformer artistTransformer;
  private final ArtistSearchResponseTransformer artistSearchResponseTransformer;

  @Override
  public Optional<ArtistDto> findArtistByDiscogsId(long discogsId) {
    return artistRepository.findByArtistDiscogsId(discogsId)
        .map(artistTransformer::transform);
  }

  @Override
  public List<ArtistDto> findAllArtistsByDiscogsIds(long... discogsIds) {
    List<ArtistEntity> artistEntities = artistRepository.findAllByArtistDiscogsIdIn(discogsIds);
    return artistEntities.stream()
        .map(artistTransformer::transform)
        .collect(Collectors.toList());
  }

  @Override
  public boolean existsArtistByDiscogsId(long discogsId) {
    return artistRepository.existsByArtistDiscogsId(discogsId);
  }

  @Override
  @Transactional
  public ArtistSearchResponse searchDiscogsByName(String artistQueryString, Pageable pageable) {
    String publicUserId = currentPublicUserIdSupplier.get();
    UserEntity userEntity = userRepository.findByPublicId(publicUserId).orElseThrow(() ->
            new ResourceNotFoundException("User with public id '" + publicUserId + "' not found!")
    );
    DiscogsArtistSearchResultDto result = discogsService.searchArtistByName(artistQueryString, pageable.getPageNumber(), pageable.getPageSize());
    result.getSearchResults().forEach(artist -> artist.setFollowed(userEntity.isFollowing(artist.getId())));

    return artistSearchResponseTransformer.transformDiscogs(result);
  }

  @Override
  @Transactional
  public ArtistSearchResponse searchSpotifyByName(String artistQueryString, Pageable pageable) {
    String publicUserId = currentPublicUserIdSupplier.get();
    UserEntity userEntity = userRepository.findByPublicId(publicUserId).orElseThrow(() ->
                                                                                        new ResourceNotFoundException("User with public id '" + publicUserId + "' not found!")
    );
    SpotifyArtistSearchResultDto result = spotifyService.searchArtists(artistQueryString, pageable.getPageNumber(), pageable.getPageSize());
//    result.getSearchResults().forEach(artist -> artist.setFollowed(userEntity.isFollowing(artist.getId()))); // ToDo NilsD: current artist id concept has to be reworked

    return artistSearchResponseTransformer.transformSpotify(result);
  }
}
