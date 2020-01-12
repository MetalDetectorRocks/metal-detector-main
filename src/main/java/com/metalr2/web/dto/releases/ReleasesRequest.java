package com.metalr2.web.dto.releases;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({
    "page",
    "size",
    "dateFrom",
    "dateTo",
    "artists"
})
public class ReleasesRequest {

  @JsonProperty("page")
  private int page;

  @JsonProperty("size")
  private int size;

  @JsonProperty("dateFrom")
  private LocalDate dateFrom;

  @JsonProperty("dateTo")
  private LocalDate dateTo;

  @JsonProperty("artists")
  private Iterable<String> artists;

}
