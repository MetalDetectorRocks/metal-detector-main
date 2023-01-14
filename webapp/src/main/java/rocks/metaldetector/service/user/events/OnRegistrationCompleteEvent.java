package rocks.metaldetector.service.user.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import rocks.metaldetector.service.user.UserDto;

@Getter
@Setter
public class OnRegistrationCompleteEvent extends ApplicationEvent {

  private final UserDto userDto;

  public OnRegistrationCompleteEvent(Object source, UserDto userDto) {
    super(source);
    this.userDto = userDto;
  }

}
