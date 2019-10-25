package com.metalr2.web.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserResponse {
	
  private String publicId;
  private String username;
  private String email;
  private boolean enabled;
	
}
