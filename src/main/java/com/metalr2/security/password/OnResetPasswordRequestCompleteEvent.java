package com.metalr2.security.password;

import com.metalr2.web.dto.UserDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class OnResetPasswordRequestCompleteEvent extends ApplicationEvent {

  private final UserDto userDto;

  public OnResetPasswordRequestCompleteEvent(Object source, UserDto userDto) {
    super(source);
    this.userDto = userDto;
  }

}
