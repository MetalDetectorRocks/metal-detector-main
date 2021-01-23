package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.service.user.UserDto;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.web.api.request.UpdateEmailRequest;
import rocks.metaldetector.web.api.request.UpdatePasswordRequest;
import rocks.metaldetector.web.api.response.UserResponse;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static rocks.metaldetector.support.Endpoints.Rest.CURRENT_USER;
import static rocks.metaldetector.support.Endpoints.Rest.CURRENT_USER_EMAIL;
import static rocks.metaldetector.support.Endpoints.Rest.CURRENT_USER_PASSWORD;

@RestController
@AllArgsConstructor
public class UserAccountRestController {

  private final UserService userService;
  private final ModelMapper mapper;

  @GetMapping(path = CURRENT_USER,
              produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<UserResponse> getCurrentUser() {
    UserDto currentUser = userService.getCurrentUser();
    UserResponse response = mapper.map(currentUser, UserResponse.class);
    return ResponseEntity.ok(response);
  }

  @PatchMapping(path = CURRENT_USER_EMAIL,
                consumes = APPLICATION_JSON_VALUE,
                produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<String> updateCurrentEmail(@Valid @RequestBody UpdateEmailRequest request) {
    UserDto updatedUserDto = userService.updateCurrentEmail(request.getEmailAddress());
    return ResponseEntity.status(HttpStatus.OK).body(updatedUserDto.getEmail());
  }

  @PatchMapping(path = CURRENT_USER_PASSWORD,
                consumes = APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updateCurrentPassword(@Valid @RequestBody UpdatePasswordRequest request) {
    userService.updateCurrentPassword(request.getOldPlainPassword(), request.getNewPlainPassword());
    return ResponseEntity.status(HttpStatus.OK).build();
  }
}
