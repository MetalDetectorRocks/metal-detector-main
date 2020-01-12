package com.metalr2.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class SearchResponse {

  private List<SearchResult> searchResults;
  private Pagination pagination;

  public SearchResponse() {
    searchResults = new ArrayList<>();
    pagination = new Pagination();
  }

  @Data
  @AllArgsConstructor
  public static class SearchResult {

    private String thumb;
    private long id;
    private String artistName;
    private Boolean isFollowed;

  }
}
