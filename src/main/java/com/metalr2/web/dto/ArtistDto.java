package com.metalr2.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistDto {

  private long artistDiscogsId;
  private String artistName;
  private String thumb;

}
