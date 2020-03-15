package rocks.metaldetector.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rocks.metaldetector.web.dto.NameSearchResultDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscogsNameSearchResponse {

  private List<NameSearchResultDto> searchResults;
  private Pagination pagination;

}
