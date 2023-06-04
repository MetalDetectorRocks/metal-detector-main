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
public class AdminDashboardResponse {

  UserInfos userInfos;
  ArtistFollowingInfos artistFollowingInfos;
  ReleaseInfos releaseInfos;
  List<ImportInfos> importInfos;
}
