package rocks.metaldetector.service.spotify;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.persistence.domain.spotify.SpotifyAuthorizationEntity;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.security.CurrentPublicUserIdSupplier;
import rocks.metaldetector.spotify.facade.SpotifyService;
import rocks.metaldetector.spotify.facade.dto.SpotifyUserAuthorizationDto;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;

@Service
@AllArgsConstructor
public class SpotifyUserAuthorizationServiceImpl implements SpotifyUserAuthorizationService {

  static final int STATE_SIZE = 10;

  private final CurrentPublicUserIdSupplier currentPublicUserIdSupplier;
  private final UserRepository userRepository;
  private final SpotifyService spotifyService;

  @Override
  @Transactional
  public String prepareAuthorization() {
    String publicUserId = currentPublicUserIdSupplier.get();
    UserEntity currentUser = userRepository.findByPublicId(publicUserId).orElseThrow(
        () -> new ResourceNotFoundException("User with public id '" + publicUserId + "' not found!")
    );

    String state = RandomStringUtils.randomAlphanumeric(STATE_SIZE);
    SpotifyAuthorizationEntity authenticationEntity = new SpotifyAuthorizationEntity(state);
    currentUser.setSpotifyAuthorization(authenticationEntity);
    userRepository.save(currentUser);

    return spotifyService.getSpotifyAuthorizationUrl() + state;
  }

  @Override
  @Transactional
  public void fetchToken(String spotifyState, String spotifyCode) {
    String publicUserId = currentPublicUserIdSupplier.get();
    UserEntity currentUser = userRepository.findByPublicId(publicUserId).orElseThrow(
        () -> new ResourceNotFoundException("User with public id '" + publicUserId + "' not found!")
    );
    SpotifyAuthorizationEntity authorizationEntity = currentUser.getSpotifyAuthorizationEntity();

    checkState(authorizationEntity, spotifyState);

    SpotifyUserAuthorizationDto authorizationDto = spotifyService.getAccessToken(spotifyCode);
    authorizationEntity.setAccessToken(authorizationDto.getAccessToken());
    authorizationEntity.setRefreshToken(authorizationDto.getRefreshToken());
    authorizationEntity.setScope(authorizationDto.getScope());
    authorizationEntity.setTokenType(authorizationDto.getTokenType());
    authorizationEntity.setExpiresIn(authorizationDto.getExpiresIn());

    userRepository.save(currentUser);
  }

  private void checkState(SpotifyAuthorizationEntity authorizationEntity, String providedState) {
    if (authorizationEntity == null || authorizationEntity.getState() == null) {
      throw new IllegalStateException("snh: spotify authorization entity is null");
    }

    if (!authorizationEntity.getState().equals(providedState)) {
      throw new IllegalArgumentException("Spotify authorization failed: state validation failed");
    }
  }
}
