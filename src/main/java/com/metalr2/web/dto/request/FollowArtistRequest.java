package com.metalr2.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowArtistRequest {

  @NotBlank
  private String artistName;
  private long artistDiscogsId;

}
