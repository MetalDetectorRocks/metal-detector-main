package com.metalr2.web.controller.mvc.authentication;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.MessageKeys;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.model.ArtifactForFramework;
import com.metalr2.model.exceptions.ResourceNotFoundException;
import com.metalr2.model.exceptions.TokenExpiredException;
import com.metalr2.model.exceptions.UserAlreadyExistsException;
import com.metalr2.model.user.events.OnRegistrationCompleteEvent;
import com.metalr2.service.token.TokenService;
import com.metalr2.service.user.UserService;
import com.metalr2.web.dto.UserDto;
import com.metalr2.web.dto.request.RegisterUserRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Controller
public class RegistrationController {

  static final String FORM_DTO = "registerUserRequest";

  private final ApplicationEventPublisher eventPublisher;
  private final UserService userService;
  private final TokenService tokenService;
  private final MessageSource messages;
  private final ModelMapper mapper;

  @Autowired
  public RegistrationController(UserService userService, TokenService tokenService, ApplicationEventPublisher eventPublisher,
                                @Qualifier("messageSource") MessageSource messages) {
    this.userService = userService;
    this.tokenService = tokenService;
    this.eventPublisher = eventPublisher;
    this.messages = messages;
    this.mapper = new ModelMapper();
  }

  @ModelAttribute(FORM_DTO)
  @ArtifactForFramework
  private RegisterUserRequest registerUserRequest() {
    return new RegisterUserRequest();
  }

  @GetMapping(Endpoints.Guest.REGISTER)
  public ModelAndView showRegistrationForm() {
    return new ModelAndView(ViewNames.Guest.REGISTER);
  }

  @PostMapping(Endpoints.Guest.REGISTER)
  public ModelAndView registerUserAccount(@Valid @ModelAttribute RegisterUserRequest registerUserRequest, BindingResult bindingResult) {
    // show registration form if there are validation errors
    if (bindingResult.hasErrors()) {
      return new ModelAndView(ViewNames.Guest.REGISTER, HttpStatus.BAD_REQUEST);
    }

    // create user
    UserDto createdUserDto;
    UserDto userDto = mapper.map(registerUserRequest, UserDto.class);

    try {
      createdUserDto = userService.createUser(userDto);
    }
    catch (UserAlreadyExistsException e) {
      if (e.getReason() == UserAlreadyExistsException.Reason.USERNAME_ALREADY_EXISTS) {
        bindingResult.rejectValue("username", "userAlreadyExists", e.getMessage());
      }
      else if (e.getReason() == UserAlreadyExistsException.Reason.EMAIL_ALREADY_EXISTS) {
        bindingResult.rejectValue("email", "userAlreadyExists", e.getMessage());
      }

      return new ModelAndView(ViewNames.Guest.REGISTER, HttpStatus.BAD_REQUEST); // show registration form with validation errors
    }

    eventPublisher.publishEvent(new OnRegistrationCompleteEvent(this, createdUserDto));

    Map<String, Object> viewModel = new HashMap<>();
    viewModel.put("successMessage", messages.getMessage(MessageKeys.Registration.SUCCESS, null, Locale.US));
    viewModel.put("registerUserRequest", new RegisterUserRequest()); // to clear the register form

    return new ModelAndView(ViewNames.Guest.REGISTER, viewModel, HttpStatus.OK);
  }

  @GetMapping(Endpoints.Guest.REGISTRATION_VERIFICATION)
  public ModelAndView verifyRegistration(@RequestParam(value = "token") String tokenString) {
    String param = "verificationSuccess";

    try {
      userService.verifyEmailToken(tokenString);
    }
    catch (TokenExpiredException e) {
      param = "tokenExpired&token=" + tokenString;
    }
    catch (ResourceNotFoundException e) {
      param = "tokenNotFound";
    }

    return new ModelAndView("redirect:" + Endpoints.Guest.LOGIN + "?" + param);
  }

  @GetMapping(Endpoints.Guest.RESEND_VERIFICATION_TOKEN)
  public ModelAndView resendEmailVerificationToken(@RequestParam(value = "token") String tokenString) {
    String param = "resendVerificationTokenSuccess";

    try {
      tokenService.resendExpiredEmailVerificationToken(tokenString);
    }
    catch (ResourceNotFoundException e) {
      param = "tokenNotFound";
    }

    return new ModelAndView("redirect:" + Endpoints.Guest.LOGIN + "?" + param);
  }
}
