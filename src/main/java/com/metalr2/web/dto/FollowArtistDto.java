package com.metalr2.web.dto;

import lombok.*;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowArtistDto {

  private String publicUserId;
  private String artistName;
  private long artistDiscogsId;

}
