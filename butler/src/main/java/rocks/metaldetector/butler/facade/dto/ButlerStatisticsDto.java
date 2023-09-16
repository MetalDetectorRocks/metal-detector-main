package rocks.metaldetector.butler.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ButlerStatisticsDto {

  private ReleaseInfoDto releaseInfo;
  private List<ImportInfoDto> importInfo;
}
