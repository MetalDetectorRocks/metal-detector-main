package com.metalr2.web.dto.discogs.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({
        "per_page",
        "items",
        "page",
        "urls",
        "pages"
})
public class DiscogsPagination {

  @JsonProperty("per_page")
  private int itemsPerPage;

  @JsonProperty("items")
  private int itemsTotal;

  @JsonProperty("page")
  private int currentPage;

  @JsonProperty("pages")
  private int pagesTotal;

  @JsonProperty("urls")
  private DiscogsPaginationUrls urls;

}
