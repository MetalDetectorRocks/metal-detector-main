package rocks.metaldetector.web.controller.rest;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.service.user.UserDto;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.web.api.request.UpdateEmailRequest;
import rocks.metaldetector.web.api.request.UpdatePasswordRequest;
import rocks.metaldetector.web.api.response.UserResponse;
import rocks.metaldetector.web.transformer.UserDtoTransformer;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static rocks.metaldetector.support.Endpoints.Rest.CURRENT_USER;
import static rocks.metaldetector.support.Endpoints.Rest.CURRENT_USER_EMAIL;
import static rocks.metaldetector.support.Endpoints.Rest.CURRENT_USER_PASSWORD;

@RestController
@AllArgsConstructor
public class UserAccountRestController {

  private final UserService userService;
  private final UserDtoTransformer userDtoTransformer;

  @GetMapping(path = CURRENT_USER, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<UserResponse> getCurrentUser() {
    UserDto currentUser = userService.getCurrentUser();
    UserResponse response = userDtoTransformer.transformUserResponse(currentUser);
    return ResponseEntity.ok(response);
  }

  @PatchMapping(path = CURRENT_USER_EMAIL, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updateCurrentEmail(@Valid @RequestBody UpdateEmailRequest request) {
    userService.updateCurrentEmail(request.getEmail());
    return ResponseEntity.ok().build();
  }

  @DeleteMapping(path = CURRENT_USER)
  public ResponseEntity<Void> deleteCurrentUser() {
    userService.deleteCurrentUser();
    return ResponseEntity.ok().build();
  }

  @PatchMapping(path = CURRENT_USER_PASSWORD, consumes = APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updateCurrentPassword(@Valid @RequestBody UpdatePasswordRequest request) {
    userService.updateCurrentPassword(request.getOldPlainPassword(), request.getNewPlainPassword());
    return ResponseEntity.status(OK).build();
  }
}
