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
public class Image {

  @JsonProperty("height")
  private int height;

  @JsonProperty("width")
  private int width;

  @JsonProperty("type")
  private String type; // todo danielw: use enum

  @JsonProperty("resource_url")
  private String resourceUrl; // todo danielw: use url data type

  @JsonProperty("uri")
  private String uri; // todo danielw: use url data type

  @JsonProperty("uri150")
  private String uri150; // todo danielw: use url data type

}
