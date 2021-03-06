package rocks.metaldetector.service.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class OnRegistrationCompleteEvent extends ApplicationEvent {

  private final UserDto userDto;

  public OnRegistrationCompleteEvent(Object source, UserDto userDto) {
    super(source);
    this.userDto = userDto;
  }

}
