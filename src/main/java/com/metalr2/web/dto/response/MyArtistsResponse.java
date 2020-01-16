package com.metalr2.web.dto.response;

import com.metalr2.web.dto.ArtistDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MyArtistsResponse {

  private List<ArtistDto> myArtists;
  private Pagination pagination;

  public MyArtistsResponse(List<ArtistDto> artists) {
    this.myArtists = artists;
  }

}
