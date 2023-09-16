package rocks.metaldetector.web.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rocks.metaldetector.butler.facade.dto.ImportInfoDto;
import rocks.metaldetector.butler.facade.dto.ReleaseInfoDto;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class StatisticsResponse {

  UserInfo userInfo;
  ArtistFollowingInfo artistFollowingInfo;
  ReleaseInfoDto releaseInfo;
  List<ImportInfoDto> importInfo;
}
