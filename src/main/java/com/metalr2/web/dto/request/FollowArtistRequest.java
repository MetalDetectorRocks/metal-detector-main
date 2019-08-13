package com.metalr2.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowArtistRequest {

  private long artistDiscogsId;

  @NotBlank
  private String emailOrUserName;

}
