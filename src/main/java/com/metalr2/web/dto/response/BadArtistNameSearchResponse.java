package com.metalr2.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BadArtistNameSearchResponse {

  private String message;
  private String artistName;
  private int page;
  private int size;
}
