package com.metalr2.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private String publicId;
  private String username;
  private String email;
  private String plainPassword;
  private boolean enabled;
  private String role;
  private LocalDateTime lastLogin;
  private LocalDateTime creationDate;

}
