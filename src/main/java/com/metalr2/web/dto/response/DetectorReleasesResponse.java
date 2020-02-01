package com.metalr2.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@Builder
public class DetectorReleasesResponse {

  private String artist;
  private List<String> additionalArtists;
  private String albumTitle;
  private LocalDate releaseDate;
  private String estimatedReleaseDate;
  private String isFollowed;

}
