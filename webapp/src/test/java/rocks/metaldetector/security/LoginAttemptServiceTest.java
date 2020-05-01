package rocks.metaldetector.security;

import org.ehcache.Cache;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class LoginAttemptServiceTest {

  @Mock
  private Cache<Integer, Long> cache;

  @InjectMocks
  private LoginAttemptService underTest;

  @AfterEach
  void tearDown() {
    reset(cache);
  }

  @Test
  @DisplayName("Cache is cleared on successful login")
  void successful_login_clears_cache() {
    // given
    int ipHash = 123;

    // when
    underTest.loginSucceeded(ipHash);

    // then
    verify(cache, times(1)).remove(ipHash);
  }
}