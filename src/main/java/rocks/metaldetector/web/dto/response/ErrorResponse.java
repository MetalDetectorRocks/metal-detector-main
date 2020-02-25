package rocks.metaldetector.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

  private LocalDateTime timestamp;
  private List<String> messages = new ArrayList<>();

  public ErrorResponse(String message) {
    this.timestamp = LocalDateTime.now();
    messages.add(message);
  }

  public ErrorResponse(List<String> messages) {
    this.timestamp = LocalDateTime.now();
    this.messages  = messages;
  }

}
