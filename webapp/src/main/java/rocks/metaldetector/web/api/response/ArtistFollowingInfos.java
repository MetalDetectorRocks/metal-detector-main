package rocks.metaldetector.web.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistFollowingInfos {

  Map<String, Long> followingsPerMonth;
  long totalFollowings;
  long followingsThisMonth;
}
