package rocks.metaldetector.service.user.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;

@Getter
public class UserCreationEvent extends ApplicationEvent {

  private final AbstractUserEntity userEntity;

  public UserCreationEvent(Object source, AbstractUserEntity userEntity) {
    super(source);
    this.userEntity = userEntity;
  }
}
