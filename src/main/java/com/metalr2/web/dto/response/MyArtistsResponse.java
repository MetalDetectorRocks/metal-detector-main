package com.metalr2.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class MyArtistsResponse {

  private List<Artist> myArtists;
  private Pagination pagination;

  public MyArtistsResponse(List<Artist> artists) {
    this.myArtists = artists;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Artist {

    private long discogsId;
    private String artistName;
    private String thumb;

  }
}
