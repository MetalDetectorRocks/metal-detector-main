package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
public class TestLoggingRestController {

  @GetMapping(path = "/rest/v1/logging/info")
  public ResponseEntity<Void> info() {
    log.info("Simple info message");
    return ResponseEntity.ok().build();
  }

  @GetMapping(path = "/rest/v1/logging/warn")
  public ResponseEntity<Void> warn() {
    log.warn("Simple warn message");
    return ResponseEntity.ok().build();
  }

  @GetMapping(path = "/rest/v1/logging/error")
  public ResponseEntity<Void> error() {
    log.error("Simple error message");
    log.error("Simple error message", new RuntimeException("oooops"));
    return ResponseEntity.ok().build();
  }

  @GetMapping(path = "/rest/v1/logging/debug")
  public ResponseEntity<Void> debug() {
    log.debug("Simple debug message");
    return ResponseEntity.ok().build();
  }

  @GetMapping(path = "/rest/v1/logging/trace")
  public ResponseEntity<Void> trace() {
    log.trace("Simple trace message");
    return ResponseEntity.ok().build();
  }
}
