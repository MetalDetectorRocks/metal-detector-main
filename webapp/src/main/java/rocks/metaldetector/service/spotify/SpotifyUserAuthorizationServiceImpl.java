package rocks.metaldetector.service.spotify;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import rocks.metaldetector.persistence.domain.spotify.SpotifyAuthorizationEntity;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.security.CurrentPublicUserIdSupplier;
import rocks.metaldetector.spotify.facade.SpotifyService;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;

@Service
@AllArgsConstructor
public class SpotifyUserAuthorizationServiceImpl implements SpotifyUserAuthorizationService {

  static final int STATE_SIZE = 10;

  private final CurrentPublicUserIdSupplier currentPublicUserIdSupplier;
  private final UserRepository userRepository;
  private final SpotifyService spotifyService;

  @Override
  public String prepareAuthorization() {
    String publicUserId = currentPublicUserIdSupplier.get();
    UserEntity currentUser = userRepository.findByPublicId(publicUserId).orElseThrow(
        () -> new ResourceNotFoundException("User with public id '" + publicUserId + "' not found!")
    );

    String state = RandomStringUtils.randomAlphanumeric(STATE_SIZE);
    SpotifyAuthorizationEntity authenticationEntity = new SpotifyAuthorizationEntity(state);
    currentUser.setSpotifyAuthorizationEntity(authenticationEntity);
    userRepository.save(currentUser);

    return spotifyService.getSpotifyAuthorizationUrl() + state;
  }
}
