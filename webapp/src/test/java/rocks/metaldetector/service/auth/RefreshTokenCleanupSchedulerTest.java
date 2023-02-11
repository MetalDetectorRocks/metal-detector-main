package rocks.metaldetector.service.auth;

import org.assertj.core.api.WithAssertions;
import org.assertj.core.data.TemporalUnitWithinOffset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.persistence.domain.user.RefreshTokenEntity;
import rocks.metaldetector.persistence.domain.user.RefreshTokenRepository;
import rocks.metaldetector.support.SecurityProperties;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenCleanupSchedulerTest implements WithAssertions {

  @Mock
  private RefreshTokenRepository refreshTokenRepository;

  @Mock
  private SecurityProperties securityProperties;

  @InjectMocks
  private RefreshTokenCleanupScheduler underTest;

  @AfterEach
  void tearDown() {
    reset(refreshTokenRepository, securityProperties);
  }

  @Test
  @DisplayName("should call refresh token repository with date threshold to find all expired refresh tokens")
  void should_call_refresh_token_repository_with_date_threshold_to_find_all_expired_refresh_tokens() {
    // given
    when(securityProperties.getRefreshTokenExpirationInMin()).thenReturn(60L);

    // when
    underTest.cleanupExpiredRefreshTokens();

    // then
    ArgumentCaptor<Date> captor = ArgumentCaptor.forClass(Date.class);
    verify(refreshTokenRepository).findAllByLastModifiedDateTimeBefore(captor.capture());
    assertThat(captor.getValue().toInstant()).isCloseTo(Instant.now().minus(1, ChronoUnit.HOURS), new TemporalUnitWithinOffset(1, ChronoUnit.SECONDS));
  }

  @Test
  @DisplayName("should call refresh token repository to delete all expired refresh tokens")
  void should_call_refresh_token_repository_to_delete_all_expired_refresh_tokens() {
    // given
    List<RefreshTokenEntity> expiredRefreshTokens = List.of(
        mock(RefreshTokenEntity.class),
        mock(RefreshTokenEntity.class)
    );
    when(refreshTokenRepository.findAllByLastModifiedDateTimeBefore(any())).thenReturn(expiredRefreshTokens);

    // when
    underTest.cleanupExpiredRefreshTokens();

    // then
    verify(refreshTokenRepository).deleteAll(expiredRefreshTokens);
  }

  @Test
  @DisplayName("should not try to delete any refresh token if no expired refresh tokens are present")
  void should_not_try_to_delete_any_refresh_token_if_no_expired_refresh_tokens_are_present() {
    // given
    List<RefreshTokenEntity> expiredRefreshTokens = Collections.emptyList();
    when(refreshTokenRepository.findAllByLastModifiedDateTimeBefore(any())).thenReturn(expiredRefreshTokens);

    // when
    underTest.cleanupExpiredRefreshTokens();

    // then
    verifyNoMoreInteractions(refreshTokenRepository);
  }
}
