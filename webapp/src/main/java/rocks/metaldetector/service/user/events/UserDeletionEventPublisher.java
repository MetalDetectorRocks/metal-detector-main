package rocks.metaldetector.service.user.events;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import rocks.metaldetector.persistence.domain.user.UserEntity;

@Component
@AllArgsConstructor
public class UserDeletionEventPublisher {

  private final ApplicationEventPublisher applicationEventPublisher;

  public void publishUserDeletionEvent(UserEntity userEntity) {
    UserDeletionEvent event = new UserDeletionEvent(this, userEntity);
    applicationEventPublisher.publishEvent(event);
  }
}
