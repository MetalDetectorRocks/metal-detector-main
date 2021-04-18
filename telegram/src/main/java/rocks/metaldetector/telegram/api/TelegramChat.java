package rocks.metaldetector.telegram.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TelegramChat {

  @JsonProperty("id")
  private int id;
}
