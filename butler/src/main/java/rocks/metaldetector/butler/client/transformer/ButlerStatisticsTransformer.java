package rocks.metaldetector.butler.client.transformer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import rocks.metaldetector.butler.api.ButlerStatisticsResponse;
import rocks.metaldetector.butler.facade.dto.ButlerStatisticsDto;

@Component
@AllArgsConstructor
public class ButlerStatisticsTransformer {

  private final ObjectMapper objectMapper;

  public ButlerStatisticsDto transform(ButlerStatisticsResponse response) {
    return objectMapper.convertValue(response, ButlerStatisticsDto.class);
  }
}
