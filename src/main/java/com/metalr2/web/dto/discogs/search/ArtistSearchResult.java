package com.metalr2.web.dto.discogs.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({
        "thumb",
        "title",
        "uri",
        "resource_url",
        "id"
})
public class ArtistSearchResult {

  private static final String DISCOGS_URI = "http://discogs.com";

  @JsonProperty("id")
  private long id;

  @JsonProperty("title")
  private String title;

  @JsonProperty("thumb")
  private String thumb;       // todo danielw: should be of type URL or something equal

  @JsonProperty("uri")
  private String uri;         // todo danielw: should be of type URI or something equal

  @JsonProperty("resource_url")
  private String resourceUrl; // todo danielw: should be of type URL or something equal

  public String getUri() {
    return DISCOGS_URI + uri;
  }
}
