package rocks.metaldetector.telegram.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TelegramMessage {

  @JsonProperty("text")
  private String text;

  @JsonProperty("chat")
  private TelegramChat chat;

  @JsonProperty("description")
  private String description;

}
