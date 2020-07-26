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
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.security.CurrentPublicUserIdSupplier;
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
  private final DiscogsService discogsService;
  private final ArtistTransformer artistTransformer;
  private final CurrentPublicUserIdSupplier currentPublicUserIdSupplier;

  @Override
  @Transactional
  public void follow(String externalId, ArtistSource source) {
    ArtistEntity artist = saveAndFetchArtist(externalId, source);
    UserEntity user = currentUser();
    user.addFollowedArtist(artist);
    userRepository.save(user);
  }

  @Override
  @Transactional
  public void unfollow(String externalId, ArtistSource source) {
    ArtistEntity artistEntity = artistRepository.findByExternalIdAndSource(externalId, source)
        .orElseThrow(() -> new ResourceNotFoundException("Artist with id '" + externalId + "' (" + source + ") not found!"));

    UserEntity user = currentUser();
    user.removeFollowedArtist(artistEntity);
    userRepository.save(user);
  }

  @Override
  @Transactional
  public List<ArtistDto> getFollowedArtistsOfCurrentUser() {
    UserEntity user = currentUser();
    return user.getFollowedArtists().stream()
            .map(artistTransformer::transform)
            .sorted(Comparator.comparing(ArtistDto::getArtistName))
            .collect(Collectors.toUnmodifiableList());
  }

  @Override
  @Transactional
  public List<ArtistDto> getFollowedArtistsOfUser(String publicUserId) {
    UserEntity user = fetchUserEntity(publicUserId);
    return user.getFollowedArtists().stream().map(artistTransformer::transform).collect(Collectors.toUnmodifiableList());
  }

  private ArtistEntity saveAndFetchArtist(String externalId, ArtistSource source) {
    if (artistRepository.existsByExternalIdAndSource(externalId, source)) {
      //noinspection OptionalGetWithoutIsPresent: call is safe due to prior existsBy check
      return artistRepository.findByExternalIdAndSource(externalId, source).get();
    }

    DiscogsArtistDto artist = discogsService.searchArtistById(externalId);
    ArtistEntity artistEntity = new ArtistEntity(artist.getId(), artist.getName(), artist.getImageUrl(), source);
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
