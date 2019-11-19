package com.metalr2.web.dto.discogs.misc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({
        "height",
        "resource_url",
        "type",
        "uri",
        "uri150",
        "width"
})
public class DiscogsImage {

  @JsonProperty("height")
  private int height;

  @JsonProperty("width")
  private int width;

  @JsonProperty("type")
  private String type; // todo danielw: use enum

  @JsonProperty("resource_url")
  private String resourceUrl;

  @JsonProperty("uri")
  private String uri;

  @JsonProperty("uri150")
  private String uri150;

}
