package rocks.metaldetector.service.artist;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.discogs.facade.DiscogsService;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistDto;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.persistence.domain.artist.ArtistRepository;
import rocks.metaldetector.persistence.domain.artist.ArtistSource;
import rocks.metaldetector.persistence.domain.artist.FollowActionEntity;
import rocks.metaldetector.persistence.domain.artist.FollowActionRepository;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.security.CurrentPublicUserIdSupplier;
import rocks.metaldetector.spotify.facade.SpotifyService;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Slf4j
public class FollowArtistServiceImpl implements FollowArtistService {

  private final UserRepository userRepository;
  private final ArtistRepository artistRepository;
  private final FollowActionRepository followActionRepository;
  private final SpotifyService spotifyService;
  private final DiscogsService discogsService;
  private final ArtistTransformer artistTransformer;
  private final CurrentPublicUserIdSupplier currentPublicUserIdSupplier;

  @Override
  @Transactional
  // ToDo DanielW: Adjust tests
  public void follow(String externalArtistId, ArtistSource source) {
    ArtistEntity artist = saveAndFetchArtist(externalArtistId, source);
    FollowActionEntity followAction = FollowActionEntity.builder()
            .user(currentUser())
            .artist(artist)
            .build();

    followActionRepository.save(followAction);
  }

  @Override
  @Transactional
  // ToDo DanielW: Adjust tests
  public void unfollow(String externalArtistId, ArtistSource source) {
    ArtistEntity artistEntity = artistRepository.findByExternalIdAndSource(externalArtistId, source)
        .orElseThrow(() -> new ResourceNotFoundException("Artist with id '" + externalArtistId + "' (" + source + ") not found!"));

    followActionRepository.deleteByUserAndArtist(currentUser(), artistEntity);
  }

  @Override
  public boolean isFollowing(String publicUserId, String externalArtistId) {
    return false; // ToDo DanielW: Implement and test
  }

  @Override
  @Transactional
  // ToDo DanielW: Adjust tests
  public List<ArtistDto> getFollowedArtistsOfCurrentUser() {
    return getFollowedArtistsOfUser(currentUser().getPublicId());
  }

  @Override
  @Transactional
  // ToDo DanielW: Adjust tests
  public List<ArtistDto> getFollowedArtistsOfUser(String publicUserId) {
    UserEntity user = fetchUserEntity(publicUserId);
    return followActionRepository.findAllByUser(user).stream()
            .map(FollowActionEntity::getArtist)
            .map(artistTransformer::transform)
            .sorted(Comparator.comparing(ArtistDto::getArtistName))
            .collect(Collectors.toUnmodifiableList());
  }

  private ArtistEntity saveAndFetchArtist(String externalId, ArtistSource source) {
    if (artistRepository.existsByExternalIdAndSource(externalId, source)) {
      //noinspection OptionalGetWithoutIsPresent: call is safe due to prior existsBy check
      return artistRepository.findByExternalIdAndSource(externalId, source).get();
    }

    ArtistEntity artistEntity = switch (source) {
      case DISCOGS -> {
        DiscogsArtistDto artist = discogsService.searchArtistById(externalId);
        yield new ArtistEntity(artist.getId(), artist.getName(), artist.getImageUrl(), source);
      }
      case SPOTIFY -> {
        SpotifyArtistDto artist = spotifyService.searchArtistById(externalId);
        yield new ArtistEntity(artist.getId(), artist.getName(), artist.getImageUrl(), source);
      }
    };

    return artistRepository.save(artistEntity);
  }

  private UserEntity currentUser() {
    return fetchUserEntity(currentPublicUserIdSupplier.get());
  }

  private UserEntity fetchUserEntity(String publicUserId) {
    return userRepository.findByPublicId(publicUserId)
        .orElseThrow(() -> new ResourceNotFoundException("User with public id '" + publicUserId + "' not found!"));
  }
}
