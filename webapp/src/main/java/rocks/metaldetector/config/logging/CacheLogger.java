package rocks.metaldetector.config.logging;

import lombok.extern.slf4j.Slf4j;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;

@Slf4j
public class CacheLogger implements CacheEventListener<String, Long> {

  @Override
  public void onEvent(CacheEvent<? extends String, ? extends Long> cacheEvent) {
    log.info("Key: {} | EventType: {} | Old value: {} | New value: {}", cacheEvent.getKey(),
             cacheEvent.getType(), cacheEvent.getOldValue(), cacheEvent.getNewValue());
  }
}
