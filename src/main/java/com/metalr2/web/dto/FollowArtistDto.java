package com.metalr2.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class FollowArtistDto {

  private String publicUserId;
  private String artistName;
  private long artistDiscogsId;

}
