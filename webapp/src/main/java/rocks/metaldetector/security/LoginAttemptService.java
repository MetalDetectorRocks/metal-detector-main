package rocks.metaldetector.security;

import lombok.AllArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LoginAttemptService {

  private static final int MAX_ATTEMPTS = 5;
  private static final String FAILED_LOGINS_CACHE = "failedLogins";

  private final CacheManager cacheManager;

  public void loginSucceeded(int ipHash) {
    Cache cache = cacheManager.getCache(FAILED_LOGINS_CACHE);
    cache.evictIfPresent(ipHash);
  }

  public void loginFailed(int ipHash) {
    Cache cache = cacheManager.getCache(FAILED_LOGINS_CACHE);
    long attempts = cache.get(ipHash) != null ? (Long) cache.get(ipHash).get() : 0;
    cache.put(ipHash, ++attempts);
  }

  public boolean isBlocked(int ipHash) {
    Cache cache = cacheManager.getCache(FAILED_LOGINS_CACHE);
    return cache.get(ipHash) != null && ((Long) cache.get(ipHash).get()) >= MAX_ATTEMPTS;
  }
}
