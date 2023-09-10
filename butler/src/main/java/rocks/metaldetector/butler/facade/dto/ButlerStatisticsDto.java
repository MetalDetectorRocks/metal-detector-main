package rocks.metaldetector.butler.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ButlerStatisticsDto {

  private ReleaseInfoDto releaseInfo;
}
