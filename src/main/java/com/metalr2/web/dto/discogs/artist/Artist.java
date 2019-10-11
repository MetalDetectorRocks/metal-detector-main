package com.metalr2.web.dto.discogs.artist;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.metalr2.web.dto.discogs.misc.Image;
import lombok.Data;
import lombok.ToString;

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
@ToString(of = {"id"})
public class Artist {

  @JsonProperty("id")
  private long id;

  @JsonProperty("profile")
  private String profile;

  @JsonProperty("releases_url")
  private String releasesUrl;

  @JsonProperty("resource_url")
  private String resourceUrl;

  @JsonProperty("uri")
  private String uri;

  @JsonProperty("urls")
  private List<String> urls;

  @JsonProperty("images")
  private List<Image> images;

  @JsonProperty("members")
  private List<Member> members;

}
