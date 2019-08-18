package com.metalr2.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BadArtistIdSearchResponse {

  private String message;
  private String artistName;
  private long artistId;
}
