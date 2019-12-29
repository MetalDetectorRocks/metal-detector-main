package com.metalr2.web.controller.rest;

import com.metalr2.service.user.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRestController {

    private final UserService userService;
    private final ModelMapper mapper;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
        this.mapper = new ModelMapper();
    }


}
