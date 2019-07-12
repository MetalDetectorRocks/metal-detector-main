package com.metalr2.web.dto.discogs.artist;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({
        "active",
        "id",
        "name",
        "resource_url"
})
public class Member {

  @JsonProperty("id")
  private long id;

  @JsonProperty("name")
  private String name;

  @JsonProperty("active")
  private boolean active;

  @JsonProperty("resource_url")
  private String resourceUrl; // todo danielw: should be of type URL or something equal

}
