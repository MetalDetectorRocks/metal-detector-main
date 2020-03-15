package rocks.metaldetector.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class NameSearchResultsDto {

  private List<NameSearchResultDto> searchResults;
  private long resultCount;

}
