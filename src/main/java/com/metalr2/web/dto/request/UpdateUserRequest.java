package com.metalr2.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateUserRequest {

  @NotBlank
  private String publicUserId;

  @NotBlank
  private String role;

  private boolean enabled;

}
