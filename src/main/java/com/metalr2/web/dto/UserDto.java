package com.metalr2.web.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private long id;
  private String publicId;
  private String username;
  private String email;
  private String plainPassword;
  private boolean enabled;

}
