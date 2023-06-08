package rocks.metaldetector.web.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistFollowingInfos {

  Map<YearMonth, Long> followingsPerMonth;
  long totalFollowings;
  long followingsThisMonth;
}
