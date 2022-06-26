package rocks.metaldetector.web.api.response;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ErrorResponse {

  private final int status;
  private final LocalDateTime timestamp;
  private final String error;
  private final List<String> messages;

  public ErrorResponse(int status, String error, List<String> messages) {
    this.timestamp = LocalDateTime.now();
    this.status = status;
    this.error = error;
    this.messages  = messages;
  }
}
