package com.metalr2.web.dto.releases;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@JsonPropertyOrder({
    "artist",
    "additionalArtists",
    "albumTitle",
    "releaseDate",
    "estimatedReleaseDate"
})
@NoArgsConstructor
public class ReleaseDto {

  @JsonProperty("artist")
  private String artist;

  @JsonProperty("additionalArtists")
  private List<String> additionalArtists;

  @JsonProperty("albumTitle")
  private String albumTitle;

  @JsonProperty("releaseDate")
  private LocalDate releaseDate;

  @JsonProperty("estimatedReleaseDate")
  private String estimatedReleaseDate;

}
