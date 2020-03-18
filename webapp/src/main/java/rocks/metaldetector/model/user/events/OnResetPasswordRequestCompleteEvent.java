package rocks.metaldetector.model.user.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import rocks.metaldetector.web.dto.UserDto;

@Getter
@Setter
public class OnResetPasswordRequestCompleteEvent extends ApplicationEvent {

  private final UserDto userDto;

  public OnResetPasswordRequestCompleteEvent(Object source, UserDto userDto) {
    super(source);
    this.userDto = userDto;
  }

}
