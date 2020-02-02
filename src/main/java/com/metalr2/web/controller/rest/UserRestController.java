package com.metalr2.web.controller.rest;

import com.metalr2.service.user.UserService;
import com.metalr2.web.dto.UserDto;
import com.metalr2.web.dto.request.RegisterUserRequest;
import com.metalr2.web.dto.response.UserResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.metalr2.config.constants.Endpoints.Rest.USERS;

@RestController
@RequestMapping(USERS)
public class UserRestController {

  private final UserService userService;
  private final ModelMapper mapper;

  @Autowired
  public UserRestController(UserService userService) {
    this.userService = userService;
    this.mapper = new ModelMapper();
  }

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

  // ToDo DanielW: Test
  @PostMapping(USERS)
  public ResponseEntity<UserResponse> createUser(@Valid @RequestBody RegisterUserRequest request) {
    UserDto userDto = mapper.map(request, UserDto.class);
    UserDto createdUserDto = userService.createAdministrator(userDto);
    UserResponse response = mapper.map(createdUserDto, UserResponse.class);

    return ResponseEntity.ok(response);
  }

}
