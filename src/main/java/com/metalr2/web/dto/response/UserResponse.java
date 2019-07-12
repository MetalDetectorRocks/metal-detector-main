package com.metalr2.web.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserResponse {
	
  private String userId;
  private String firstName;
  private String lastName;
  private String email;
  private boolean enabled;

  public String getName() {
    return String.join(" ", firstName, lastName);
  }
	
}
