package rocks.metaldetector.telegram.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TelegramChat {

  @JsonProperty("id")
  private int id;
}
