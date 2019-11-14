package com.metalr2.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowArtistRequest {

  private String publicUserId;
  private long artistDiscogsId;

}
