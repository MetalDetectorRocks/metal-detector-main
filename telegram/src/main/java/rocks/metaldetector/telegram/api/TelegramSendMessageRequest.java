package rocks.metaldetector.telegram.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelegramSendMessageRequest {

  @JsonProperty("chat_id")
  private int chatId;

  @JsonProperty("text")
  private String text;
}
