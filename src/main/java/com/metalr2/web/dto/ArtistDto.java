package com.metalr2.web.dto;

import lombok.*;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistDto {

  private long artistDiscogsId;
  private String artistName;
  private String thumb;

}
