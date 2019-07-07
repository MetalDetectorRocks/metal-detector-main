package com.metalr2.discogs.model.artist;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.metalr2.discogs.model.misc.Image;
import lombok.Data;
import java.util.List;

@Data
@JsonPropertyOrder({
        "profile",
        "releases_url",
        "resource_url",
        "uri",
        "urls",
        "id",
        "images",
        "members"
})
public class Artist {

  @JsonProperty("id")
  private long id;

  // todo danielw: name?
//  @JsonProperty("")
//  private String name;

  @JsonProperty("profile")
  private String profile;

  @JsonProperty("releases_url")
  private String releasesUrl; // todo danielw: use url data type

  @JsonProperty("resource_url")
  private String resourceUrl; // todo danielw: use url data type

  @JsonProperty("uri")
  private String uri; // todo danielw: use url data type

  @JsonProperty("urls")
  private List<String> urls; // todo danielw: use url data type

  @JsonProperty("images")
  private List<Image> images;

  @JsonProperty("members")
  private List<Member> members;

}
