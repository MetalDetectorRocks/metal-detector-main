package rocks.metaldetector.web.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class StatisticsResponse {

  UserInfo userInfo;
  ArtistFollowingInfo artistFollowingInfo;
  ReleaseInfo releaseInfo;
  List<ImportInfo> importInfo;
}
