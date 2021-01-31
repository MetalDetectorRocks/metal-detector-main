package rocks.metaldetector.service.spotify;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.persistence.domain.spotify.SpotifyAuthorizationEntity;
import rocks.metaldetector.persistence.domain.spotify.SpotifyAuthorizationRepository;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.security.CurrentUserSupplier;
import rocks.metaldetector.spotify.facade.SpotifyService;
import rocks.metaldetector.spotify.facade.dto.SpotifyUserAuthorizationDto;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SpotifyUserAuthorizationServiceImpl implements SpotifyUserAuthorizationService {

  static final int STATE_SIZE = 10;
  static final int GRACE_PERIOD_SECONDS = 60;

  private final CurrentUserSupplier currentUserSupplier;
  private final SpotifyAuthorizationRepository spotifyAuthorizationRepository;
  private final SpotifyService spotifyService;

  @Override
  @Transactional(readOnly = true)
  public boolean exists() {
    UserEntity currentUser = currentUserSupplier.get();
    Optional<SpotifyAuthorizationEntity> authorizationEntity = spotifyAuthorizationRepository.findByUserId(currentUser.getId());
    return authorizationEntity.isPresent()
            && authorizationEntity.get().getAccessToken() != null
            && authorizationEntity.get().getRefreshToken() != null;
  }

  @Override
  @Transactional
  public String prepareAuthorization() {
    UserEntity currentUser = currentUserSupplier.get();
    Optional<SpotifyAuthorizationEntity> authorizationEntity = spotifyAuthorizationRepository.findByUserId(currentUser.getId());

    String state;
    if (authorizationEntity.isEmpty()) {
      state = RandomStringUtils.randomAlphanumeric(STATE_SIZE);
      SpotifyAuthorizationEntity authenticationEntity = new SpotifyAuthorizationEntity(currentUser, state);
      spotifyAuthorizationRepository.save(authenticationEntity);
    }
    else {
      state = authorizationEntity.get().getState();
    }

    return spotifyService.getSpotifyAuthorizationUrl() + state;
  }

  @Override
  @Transactional
  public void persistInitialToken(String spotifyState, String spotifyCode) {
    SpotifyAuthorizationEntity authorizationEntity = findAuthorizationEntityFromCurrentUser();
    checkState(authorizationEntity, spotifyState);

    LocalDateTime now = LocalDateTime.now();
    SpotifyUserAuthorizationDto authorizationDto = spotifyService.getAccessToken(spotifyCode);
    authorizationEntity.setAccessToken(authorizationDto.getAccessToken());
    authorizationEntity.setRefreshToken(authorizationDto.getRefreshToken());
    authorizationEntity.setScope(authorizationDto.getScope());
    authorizationEntity.setTokenType(authorizationDto.getTokenType());
    authorizationEntity.setExpiresAt(now.plusSeconds(authorizationDto.getExpiresIn() - GRACE_PERIOD_SECONDS));

    spotifyAuthorizationRepository.save(authorizationEntity);
  }

  @Override
  @Transactional
  public String getOrRefreshToken() {
    SpotifyAuthorizationEntity authorizationEntity = findAuthorizationEntityFromCurrentUser();
    if (authorizationEntity.getRefreshToken() == null || authorizationEntity.getRefreshToken().isEmpty()) {
      throw new IllegalStateException("refresh token is empty");
    }
    LocalDateTime now = LocalDateTime.now();

    if (authorizationEntity.getExpiresAt().isAfter(now)) {
      return authorizationEntity.getAccessToken();
    }

    SpotifyUserAuthorizationDto refreshedToken = spotifyService.refreshToken(authorizationEntity.getRefreshToken());
    authorizationEntity.setAccessToken(refreshedToken.getAccessToken());
    authorizationEntity.setScope(refreshedToken.getScope());
    authorizationEntity.setTokenType(refreshedToken.getTokenType());
    authorizationEntity.setExpiresAt(now.plusSeconds(refreshedToken.getExpiresIn() - GRACE_PERIOD_SECONDS));
    spotifyAuthorizationRepository.save(authorizationEntity);

    return refreshedToken.getAccessToken();
  }

  @Override
  @Transactional
  public void deleteAuthorization() {
    UserEntity currentUser = currentUserSupplier.get();
    Optional<SpotifyAuthorizationEntity> authorizationEntityOptional = spotifyAuthorizationRepository.findByUserId(currentUser.getId());
    authorizationEntityOptional.ifPresent(spotifyAuthorizationRepository::delete);
  }

  private SpotifyAuthorizationEntity findAuthorizationEntityFromCurrentUser() {
    UserEntity currentUser = currentUserSupplier.get();
    Optional<SpotifyAuthorizationEntity> authorizationEntityOptional = spotifyAuthorizationRepository.findByUserId(currentUser.getId());
    return authorizationEntityOptional.orElseThrow(
            () -> new IllegalStateException("no authorization entity exists although it should!")
    );
  }

  private void checkState(SpotifyAuthorizationEntity authorizationEntity, String providedState) {
    if (authorizationEntity.getState() == null) {
      throw new IllegalStateException("snh: spotify authorization entity or state is null");
    }

    if (!authorizationEntity.getState().equals(providedState)) {
      throw new IllegalArgumentException("Spotify authorization failed: state validation failed");
    }
  }
}
