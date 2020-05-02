package rocks.metaldetector.security;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rocks.metaldetector.security.LoginAttemptService.FAILED_LOGINS_CACHE;

@ExtendWith(MockitoExtension.class)
class LoginAttemptServiceTest implements WithAssertions {

  @Mock
  private CacheManager cacheManager;

  @Mock
  private Cache cache;

  @Mock
  private Cache.ValueWrapper valueWrapper;

  @InjectMocks
  private LoginAttemptService underTest;

  @BeforeEach
  void setup() {
    when(cacheManager.getCache(FAILED_LOGINS_CACHE)).thenReturn(cache);
  }

  @AfterEach
  void tearDown() {
    reset(cacheManager, cache, valueWrapper);
  }

  @Test
  @DisplayName("Cache is fetched from manager on failed login")
  void failed_login_gets_cache_from_manager() {
    // when
    underTest.loginFailed("666");

    // then
    verify(cacheManager, times(1)).getCache(FAILED_LOGINS_CACHE);
  }

  @Test
  @DisplayName("Cache is fetched from manager to check for blocked user")
  void checking_blocked_users_gets_cache_from_manager() {
    // when
    underTest.isBlocked("666");

    // then
    verify(cacheManager, times(1)).getCache(FAILED_LOGINS_CACHE);
  }

  @Test
  @DisplayName("User is blocked if the cached value is bigger than 4")
  void user_counts_as_blocked() {
    // given
    String ipHash = "666";
    when(cache.get(ipHash)).thenReturn(valueWrapper);
    when(valueWrapper.get()).thenReturn(5);

    // when
    var result = underTest.isBlocked(ipHash);

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("User is blocked if the cache could not be fetched")
  void user_counts_as_blocked_if_cache_not_present() {
    // given
    when(cacheManager.getCache(FAILED_LOGINS_CACHE)).thenReturn(null);

    // when
    var result = underTest.isBlocked("666");

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("User is not blocked if the cached value is smaller than 5")
  void user_does_not_count_as_blocked() {
    // given
    String ipHash = "666";
    when(cache.get(ipHash)).thenReturn(valueWrapper);
    when(valueWrapper.get()).thenReturn(1);

    // when
    var result = underTest.isBlocked(ipHash);

    // then
    assertThat(result).isFalse();
  }
}