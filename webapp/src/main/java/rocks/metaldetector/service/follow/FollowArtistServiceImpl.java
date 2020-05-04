package rocks.metaldetector.service.follow;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.persistence.domain.artist.ArtistRepository;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.security.CurrentUserSupplier;
import rocks.metaldetector.service.artist.ArtistDto;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class FollowArtistServiceImpl implements FollowArtistService {

  private final UserRepository userRepository;
  private final ArtistRepository artistRepository;
  private final CurrentUserSupplier currentUserSupplier;

  @Override
  public void follow(long artistId) {
    Optional<ArtistEntity> artistOptional = artistRepository.findByArtistDiscogsId(artistId);

    if (artistOptional.isPresent()) {
      UserEntity user = currentUserSupplier.get();
      user.addFollowedArtist(artistOptional.get());
      userRepository.save(user);
    }
  }

  @Override
  public void unfollow(long artistId) {
    Optional<ArtistEntity> artistOptional = artistRepository.findByArtistDiscogsId(artistId);

    if (artistOptional.isPresent()) {
      UserEntity user = currentUserSupplier.get();
      user.removeFollowedArtist(artistOptional.get());
      userRepository.save(user);
    }
  }

  @Override
  public boolean isFollowed(long artistId) {
    Optional<ArtistEntity> artistOptional = artistRepository.findByArtistDiscogsId(artistId);

    if (artistOptional.isPresent()) {
      UserEntity user = currentUserSupplier.get();
      return user.getFollowedArtists().contains(artistOptional.get());
    }
    return false;
  }

  @Override
  public List<ArtistDto> findFollowedArtists() {
    UserEntity user = currentUserSupplier.get();
    return user.getFollowedArtists().stream().map(this::createArtistDto).collect(Collectors.toUnmodifiableList());
  }

  @Override
  public List<ArtistDto> findFollowedArtistsForUser(String publicUserId) {
    Optional<UserEntity> userOptional = userRepository.findByPublicId(publicUserId);

    return userOptional.map(userEntity -> userEntity.getFollowedArtists().stream().map(this::createArtistDto).collect(Collectors.toUnmodifiableList()))
                       .orElse(Collections.emptyList());
  }

  @Override
  public long countFollowedArtists() {
    UserEntity user = currentUserSupplier.get();
    return user.getFollowedArtists().size();
  }

  private ArtistDto createArtistDto(ArtistEntity artistEntity) {
    return ArtistDto.builder()
        .discogsId(artistEntity.getArtistDiscogsId())
        .artistName(artistEntity.getArtistName())
        .thumb(artistEntity.getThumb())
        .build();
  }
}
