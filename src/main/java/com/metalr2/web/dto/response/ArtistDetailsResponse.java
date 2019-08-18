package com.metalr2.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ArtistDetailsResponse {

  private String artistName;
  private String profile;
  private List<String> activeMember;
  private List<String> formerMember;
  private List<String> images;

}
