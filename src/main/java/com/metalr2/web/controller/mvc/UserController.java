package com.metalr2.web.controller.mvc;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.service.user.UserService;
import com.metalr2.web.dto.response.UserResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class UserController {

  private final UserService userService;
  private final ModelMapper mapper;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
    this.mapper = new ModelMapper();
  }

  @GetMapping({Endpoints.AdminArea.USERS_LIST})
  public ModelAndView showUsersList() {
    List<UserResponse> users = userService.getAllUsers()
            .stream()
            .map(userDto -> mapper.map(userDto, UserResponse.class))
            .collect(Collectors.toList());

    Map<String, Object> viewModel = new HashMap<>();
    viewModel.put("users", users);

    return new ModelAndView(ViewNames.AdminArea.USERS_LIST, viewModel);
  }

}
