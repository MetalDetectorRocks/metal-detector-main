package com.metalr2.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ArtistNameSearchResponse {

  private String artistName;
  private List<ArtistSearchResult> artistSearchResults;
  private Pagination pagination;

  public ArtistNameSearchResponse() {
    artistName = "";
    pagination = new Pagination();
  }

  @Data
  @AllArgsConstructor
  public static class ArtistSearchResult {

    private String thumb;
    private long id;
    private String artistName;

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Pagination {

    private int totalPages;
    private int currentPage;
    private int nextSize;
    private int nextPage;
    private List<Integer> pageNumbers;
  }
}
