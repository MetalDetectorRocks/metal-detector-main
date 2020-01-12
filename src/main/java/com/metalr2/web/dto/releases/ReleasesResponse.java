package com.metalr2.web.dto.releases;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({
    "currentPage",
    "size",
    "totalPages",
    "totalReleases",
    "releases"
})
@NoArgsConstructor
public class ReleasesResponse {

  @JsonProperty("currentPage")
  private int currentPage;

  @JsonProperty("size")
  private int size;

  @JsonProperty("totalPages")
  private int totalPages;

  @JsonProperty("totalReleases")
  private long totalReleases;

  @JsonProperty("releases")
  private Iterable<ReleaseDto> releases;

}
