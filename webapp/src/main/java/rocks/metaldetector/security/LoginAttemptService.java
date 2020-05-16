package rocks.metaldetector.security;

import lombok.AllArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LoginAttemptService {

  private static final int MAX_ATTEMPTS = 5;
  public static final String FAILED_LOGINS_CACHE = "failedLogins";

  private final CacheManager cacheManager;

  @CacheEvict(value = FAILED_LOGINS_CACHE)
  public void loginSucceeded(String ipHash) {
  }

  public void loginFailed(String ipHash) {
    Cache cache = cacheManager.getCache(FAILED_LOGINS_CACHE);

    if (cache != null) {
      Cache.ValueWrapper valueWrapper = cache.get(ipHash);
      int attempts = valueWrapper != null ? (int) valueWrapper.get() : 0;
      cache.put(ipHash, ++attempts);
    }
  }

  public boolean isBlocked(String ipHash) {
    Cache cache = cacheManager.getCache(FAILED_LOGINS_CACHE);

    if (cache != null) {
      Cache.ValueWrapper valueWrapper = cache.get(ipHash);
      return valueWrapper != null && (int) valueWrapper.get() >= MAX_ATTEMPTS;
    }
    return true;
  }
}
