package rocks.metaldetector.service.user.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import rocks.metaldetector.service.user.UserDto;

@Getter
@Setter
public class OnResetPasswordRequestCompleteEvent extends ApplicationEvent {

  private final UserDto userDto;

  public OnResetPasswordRequestCompleteEvent(Object source, UserDto userDto) {
    super(source);
    this.userDto = userDto;
  }

}
