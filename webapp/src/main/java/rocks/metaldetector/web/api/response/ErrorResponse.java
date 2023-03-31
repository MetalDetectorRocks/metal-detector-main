package rocks.metaldetector.web.api.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ErrorResponse {

  private int status;
  private LocalDateTime timestamp;
  private String error;
  private List<String> messages;

  public ErrorResponse(int status, String error, List<String> messages) {
    this.timestamp = LocalDateTime.now();
    this.status = status;
    this.error = error;
    this.messages  = messages;
  }
}
