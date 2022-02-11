package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.service.user.UserDto;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.web.api.request.RegisterUserRequest;
import rocks.metaldetector.web.api.request.UpdateUserRequest;
import rocks.metaldetector.web.api.response.UserResponse;
import rocks.metaldetector.web.transformer.UserDtoTransformer;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static rocks.metaldetector.support.Endpoints.Rest.USERS;

@RestController
@RequestMapping(USERS)
@AllArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
public class UserRestController {

  private final UserService userService;
  private final UserDtoTransformer userDtoTransformer;

  @GetMapping(produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<List<UserResponse>> getAllUsers() {
    List<UserResponse> response = userService.getAllUsers().stream()
            .map(userDtoTransformer::transformUserResponse)
            .collect(Collectors.toList());

    return ResponseEntity.ok(response);
  }

  @GetMapping(path = "/{id}",
              produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<UserResponse> getUser(@PathVariable(name = "id") String publicUserId) {
    UserDto userDto = userService.getUserByPublicId(publicUserId);
    UserResponse response = userDtoTransformer.transformUserResponse(userDto);

    return ResponseEntity.ok(response);
  }

  @PostMapping(consumes = APPLICATION_JSON_VALUE,
               produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<UserResponse> createAdministrator(@Valid @RequestBody RegisterUserRequest request) {
    UserDto userDto = userDtoTransformer.transformUserDto(request);
    UserDto createdUserDto = userService.createAdministrator(userDto);
    UserResponse response = userDtoTransformer.transformUserResponse(createdUserDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PutMapping(consumes = APPLICATION_JSON_VALUE,
              produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UpdateUserRequest request) {
    UserDto userDto = userDtoTransformer.transformUserDto(request);
    UserDto updatedUserDto = userService.updateUser(request.getPublicUserId(), userDto);
    UserResponse response = userDtoTransformer.transformUserResponse(updatedUserDto);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
