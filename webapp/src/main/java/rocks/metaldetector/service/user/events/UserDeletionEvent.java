package rocks.metaldetector.service.user.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import rocks.metaldetector.persistence.domain.user.UserEntity;

@Getter
public class UserDeletionEvent extends ApplicationEvent {

  private final UserEntity userEntity;

  public UserDeletionEvent(Object source, UserEntity userEntity) {
    super(source);
    this.userEntity = userEntity;
  }
}
