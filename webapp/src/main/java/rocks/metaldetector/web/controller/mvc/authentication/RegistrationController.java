package rocks.metaldetector.web.controller.mvc.authentication;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.service.exceptions.TokenExpiredException;
import rocks.metaldetector.service.exceptions.UserAlreadyExistsException;
import rocks.metaldetector.service.token.TokenService;
import rocks.metaldetector.service.user.OnRegistrationCompleteEvent;
import rocks.metaldetector.service.user.UserDto;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;
import rocks.metaldetector.support.infrastructure.ArtifactForFramework;
import rocks.metaldetector.web.api.request.RegisterUserRequest;
import rocks.metaldetector.web.transformer.UserDtoTransformer;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static rocks.metaldetector.service.exceptions.UserAlreadyExistsException.Reason.EMAIL_ALREADY_EXISTS;
import static rocks.metaldetector.service.exceptions.UserAlreadyExistsException.Reason.USERNAME_ALREADY_EXISTS;
import static rocks.metaldetector.support.Endpoints.Authentication.LOGIN;
import static rocks.metaldetector.support.Endpoints.Authentication.REGISTER;
import static rocks.metaldetector.support.Endpoints.Authentication.REGISTRATION_VERIFICATION;
import static rocks.metaldetector.support.Endpoints.Authentication.RESEND_VERIFICATION_TOKEN;

@Controller
@AllArgsConstructor
@Profile("!preview")
public class RegistrationController {

  static final String FORM_DTO = "registerUserRequest";

  private final ApplicationEventPublisher eventPublisher;
  private final UserService userService;
  private final TokenService tokenService;
  private final UserDtoTransformer userDtoTransformer;

  @ModelAttribute(FORM_DTO)
  @ArtifactForFramework
  private RegisterUserRequest registerUserRequest() {
    return new RegisterUserRequest();
  }

  @GetMapping(REGISTER)
  public ModelAndView showRegistrationForm() {
    return new ModelAndView(ViewNames.Authentication.REGISTER);
  }

  @PostMapping(REGISTER)
  public ModelAndView registerUserAccount(@Valid @ModelAttribute RegisterUserRequest registerUserRequest, BindingResult bindingResult) {
    // show registration form if there are validation errors
    if (bindingResult.hasErrors()) {
      return new ModelAndView(ViewNames.Authentication.REGISTER, BAD_REQUEST);
    }

    // create user
    UserDto createdUserDto;
    UserDto userDto = userDtoTransformer.transformUserDto(registerUserRequest);

    try {
      createdUserDto = userService.createUser(userDto);
    }
    catch (UserAlreadyExistsException e) {
      if (e.getReason() == USERNAME_ALREADY_EXISTS) {
        bindingResult.rejectValue("username", "userAlreadyExists", e.getMessage());
      }
      else if (e.getReason() == EMAIL_ALREADY_EXISTS) {
        bindingResult.rejectValue("email", "userAlreadyExists", e.getMessage());
      }

      return new ModelAndView(ViewNames.Authentication.REGISTER, BAD_REQUEST); // show registration form with validation errors
    }

    eventPublisher.publishEvent(new OnRegistrationCompleteEvent(this, createdUserDto));

    Map<String, Object> viewModel = new HashMap<>();
    viewModel.put("isSuccessful", true);
    viewModel.put("registerUserRequest", new RegisterUserRequest()); // to clear the register form

    return new ModelAndView(ViewNames.Authentication.REGISTER, viewModel, OK);
  }

  @GetMapping(REGISTRATION_VERIFICATION)
  public ModelAndView verifyRegistration(@RequestParam(value = "token") String tokenString) {
    String param = "verificationSuccess";

    try {
      userService.verifyEmailToken(tokenString);
    }
    catch (TokenExpiredException e) {
      param = "tokenExpired&token=" + tokenString;
    }
    catch (ResourceNotFoundException e) {
      param = "userNotFound";
    }

    return new ModelAndView("redirect:" + LOGIN + "?" + param);
  }

  @GetMapping(RESEND_VERIFICATION_TOKEN)
  public ModelAndView resendEmailVerificationToken(@RequestParam(value = "token") String tokenString) {
    String param = "resendVerificationTokenSuccess";

    try {
      tokenService.resendExpiredEmailVerificationToken(tokenString);
    }
    catch (ResourceNotFoundException e) {
      param = "userNotFound";
    }

    return new ModelAndView("redirect:" + LOGIN + "?" + param);
  }
}
