package rocks.metaldetector.service.follow;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.discogs.facade.DiscogsService;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistDto;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.persistence.domain.artist.ArtistRepository;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.security.CurrentUserSupplier;

@AllArgsConstructor
@Component
@Slf4j
public class FollowArtistServiceImpl implements FollowArtistService {

  private final UserRepository userRepository;
  private final ArtistRepository artistRepository;
  private final DiscogsService discogsService;
  private final CurrentUserSupplier currentUserSupplier;

  @Override
  @Transactional
  public void follow(long discogsId) {
    ArtistEntity artist = saveAndFetchArtist(discogsId);
    UserEntity user = currentUserSupplier.get();
    user.addFollowedArtist(artist);
    userRepository.save(user);
  }

  @Override
  @Transactional
  public void unfollow(long artistId) {
    //noinspection OptionalGetWithoutIsPresent
    ArtistEntity artistEntity = artistRepository.findByArtistDiscogsId(artistId).get();
    UserEntity user = currentUserSupplier.get();
    user.removeFollowedArtist(artistEntity);
    userRepository.save(user);
  }

  private ArtistEntity saveAndFetchArtist(long discogsId) {
    if (artistRepository.existsByArtistDiscogsId(discogsId)) {
      //noinspection OptionalGetWithoutIsPresent
      return artistRepository.findByArtistDiscogsId(discogsId).get();
    }

    DiscogsArtistDto artist = discogsService.searchArtistById(discogsId);
    ArtistEntity artistEntity = new ArtistEntity(artist.getId(), artist.getName(), artist.getImageUrl());
    return artistRepository.save(artistEntity);
  }
}
