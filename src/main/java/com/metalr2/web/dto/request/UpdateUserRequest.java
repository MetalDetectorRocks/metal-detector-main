package com.metalr2.web.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
@Setter
public class UpdateUserRequest {

  @NotBlank
  private String firstName;

  @NotBlank
  private String lastName;

}
