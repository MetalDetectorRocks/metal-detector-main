package rocks.metaldetector.model.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import rocks.metaldetector.web.dto.UserDto;

@Getter
@Setter
public class OnRegistrationCompleteEvent extends ApplicationEvent {

  private final UserDto userDto;

  public OnRegistrationCompleteEvent(Object source, UserDto userDto) {
    super(source);
    this.userDto = userDto;
  }

}
