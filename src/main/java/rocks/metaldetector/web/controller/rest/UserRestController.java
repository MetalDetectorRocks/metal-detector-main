package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.web.dto.UserDto;
import rocks.metaldetector.web.dto.request.RegisterUserRequest;
import rocks.metaldetector.web.dto.request.UpdateUserRequest;
import rocks.metaldetector.web.dto.response.UserResponse;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static rocks.metaldetector.config.constants.Endpoints.Rest.USERS;

@RestController
@RequestMapping(USERS)
@AllArgsConstructor
@Secured("ROLE_ADMINISTRATOR")
public class UserRestController {

  private final UserService userService;
  private final ModelMapper mapper;

  @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<List<UserResponse>> getAllUsers() {
    List<UserResponse> response = userService.getAllUsers().stream()
            .map(userDto -> mapper.map(userDto, UserResponse.class))
            .collect(Collectors.toList());

    return ResponseEntity.ok(response);
  }

  @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<UserResponse> getUser(@PathVariable(name = "id") String publicUserId) {
    UserDto userDto = userService.getUserByPublicId(publicUserId);
    UserResponse response = mapper.map(userDto, UserResponse.class);

    return ResponseEntity.ok(response);
  }

  @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
               produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<UserResponse> createUser(@Valid @RequestBody RegisterUserRequest request) {
    UserDto userDto = mapper.map(request, UserDto.class);
    UserDto createdUserDto = userService.createAdministrator(userDto);
    UserResponse response = mapper.map(createdUserDto, UserResponse.class);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PutMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
              produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UpdateUserRequest request) {
    UserDto userDto = mapper.map(request, UserDto.class);
    UserDto updatedUserDto = userService.updateUser(request.getPublicUserId(), userDto);
    UserResponse response = mapper.map(updatedUserDto, UserResponse.class);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
