package rocks.metaldetector.service.spotify;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.persistence.domain.spotify.SpotifyAuthorizationEntity;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.security.CurrentUserSupplier;
import rocks.metaldetector.spotify.facade.SpotifyService;
import rocks.metaldetector.spotify.facade.dto.SpotifyUserAuthorizationDto;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class SpotifyUserAuthorizationServiceImpl implements SpotifyUserAuthorizationService {

  static final int STATE_SIZE = 10;

  private final CurrentUserSupplier currentUserSupplier;
  private final UserRepository userRepository;
  private final SpotifyService spotifyService;

  @Override
  @Transactional
  public String prepareAuthorization() {
    UserEntity currentUser = currentUserSupplier.get();

    String state = RandomStringUtils.randomAlphanumeric(STATE_SIZE);
    SpotifyAuthorizationEntity authenticationEntity = new SpotifyAuthorizationEntity(state);
    currentUser.setSpotifyAuthorization(authenticationEntity);
    userRepository.save(currentUser);

    return spotifyService.getSpotifyAuthorizationUrl() + state;
  }

  @Override
  @Transactional
  public void fetchInitialToken(String spotifyState, String spotifyCode) {
    UserEntity currentUser = currentUserSupplier.get();
    SpotifyAuthorizationEntity authorizationEntity = currentUser.getSpotifyAuthorization();

    checkState(authorizationEntity, spotifyState);

    SpotifyUserAuthorizationDto authorizationDto = spotifyService.getAccessToken(spotifyCode);
    authorizationEntity.setAccessToken(authorizationDto.getAccessToken());
    authorizationEntity.setRefreshToken(authorizationDto.getRefreshToken());
    authorizationEntity.setScope(authorizationDto.getScope());
    authorizationEntity.setTokenType(authorizationDto.getTokenType());
    authorizationEntity.setExpiresIn(authorizationDto.getExpiresIn());

    userRepository.save(currentUser);
  }

  @Override
  @Transactional
  public String getOrRefreshToken() {
    UserEntity currentUser = currentUserSupplier.get();
    SpotifyAuthorizationEntity authorizationEntity = currentUser.getSpotifyAuthorization();

    if (authorizationEntity == null || authorizationEntity.getRefreshToken() == null || authorizationEntity.getRefreshToken().isEmpty()) {
      throw new IllegalStateException("refresh token is empty");
    }

    if (authorizationEntity.getCreatedDateTime().plusSeconds(authorizationEntity.getExpiresIn()).isAfter(LocalDateTime.now())) {
      return authorizationEntity.getAccessToken();
    }

    SpotifyUserAuthorizationDto refreshedToken = spotifyService.refreshToken(authorizationEntity.getRefreshToken());
    authorizationEntity.setAccessToken(refreshedToken.getAccessToken());
    authorizationEntity.setScope(refreshedToken.getScope());
    authorizationEntity.setTokenType(refreshedToken.getTokenType());
    authorizationEntity.setExpiresIn(refreshedToken.getExpiresIn());

    userRepository.save(currentUser);

    return refreshedToken.getAccessToken();
  }

  private void checkState(SpotifyAuthorizationEntity authorizationEntity, String providedState) {
    if (authorizationEntity == null || authorizationEntity.getState() == null) {
      throw new IllegalStateException("snh: spotify authorization entity or state is null");
    }

    if (!authorizationEntity.getState().equals(providedState)) {
      throw new IllegalArgumentException("Spotify authorization failed: state validation failed");
    }
  }
}
