package rocks.metaldetector.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NameSearchResultDto {

  private String thumb;
  private long id;
  private String artistName;
  private Boolean isFollowed;

}
