package rocks.metaldetector.service.artist;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.discogs.facade.DiscogsService;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistDto;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.persistence.domain.artist.ArtistRepository;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.security.CurrentPublicUserIdSupplier;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;

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
  public void follow(long discogsId) {
    ArtistEntity artist = saveAndFetchArtist(discogsId);
    UserEntity user = currentUser();
    user.addFollowedArtist(artist);
    userRepository.save(user);
  }

  @Override
  @Transactional
  public void unfollow(long artistId) {
    ArtistEntity artistEntity = artistRepository.findByArtistDiscogsId(artistId).orElseThrow(() ->
      new ResourceNotFoundException("Artist with id '" + artistId + "' not found!")
    );

    UserEntity user = currentUser();
    user.removeFollowedArtist(artistEntity);
    userRepository.save(user);
  }

  @Transactional
  public List<ArtistDto> getFollowedArtistsOfCurrentUser() {
    UserEntity user = currentUser();
    return user.getFollowedArtists().stream().map(artistTransformer::transform).collect(Collectors.toUnmodifiableList());
  }

  private ArtistEntity saveAndFetchArtist(long discogsId) {
    if (artistRepository.existsByArtistDiscogsId(discogsId)) {
      //noinspection OptionalGetWithoutIsPresent: call is safe due to prior existsBy check
      return artistRepository.findByArtistDiscogsId(discogsId).get();
    }

    DiscogsArtistDto artist = discogsService.searchArtistById(discogsId);
    ArtistEntity artistEntity = new ArtistEntity(artist.getId(), artist.getName(), artist.getImageUrl());
    return artistRepository.save(artistEntity);
  }

  private UserEntity currentUser() {
    String publicUserId = currentPublicUserIdSupplier.get();
    return userRepository.findByPublicId(publicUserId).orElseThrow(() ->
            new ResourceNotFoundException("User with public id '" + publicUserId + "' not found!")
    );
  }
}
