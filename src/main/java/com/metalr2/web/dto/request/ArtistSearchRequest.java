package com.metalr2.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistSearchRequest {

  @NotBlank
  private String publicUserId;
  @NotBlank
  private String artistName;
  private int page;
  private int size;

}
