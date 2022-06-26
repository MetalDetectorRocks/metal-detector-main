package rocks.metaldetector.telegram.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelegramMessage {

  @JsonProperty("text")
  private String text;

  @JsonProperty("chat")
  private TelegramChat chat;

  @JsonProperty("description")
  private String description;

}
