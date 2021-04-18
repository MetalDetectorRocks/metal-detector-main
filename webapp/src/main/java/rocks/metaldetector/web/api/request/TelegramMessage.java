package rocks.metaldetector.web.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelegramMessage {

  @NotBlank
  private String text;

  @NotNull
  private @Valid TelegramChat chat;
}
