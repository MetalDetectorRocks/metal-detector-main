package rocks.metaldetector.web.controller.mvc.authentication;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.service.user.events.OnResetPasswordRequestCompleteEvent;
import rocks.metaldetector.service.user.UserDto;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.support.infrastructure.ArtifactForFramework;
import rocks.metaldetector.web.api.request.ForgotPasswordRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static rocks.metaldetector.support.Endpoints.Authentication.FORGOT_PASSWORD;

@Controller
@RequestMapping(FORGOT_PASSWORD)
@AllArgsConstructor
public class ForgotPasswordController {

  static final String FORM_DTO = "forgotPasswordRequest";

  private final ApplicationEventPublisher eventPublisher;
  private final UserService userService;

  @ModelAttribute(FORM_DTO)
  @ArtifactForFramework
  private ForgotPasswordRequest forgotPasswordRequest() {
    return new ForgotPasswordRequest();
  }

  @GetMapping
  public ModelAndView showForgotPasswordForm() {
    return new ModelAndView(ViewNames.Authentication.FORGOT_PASSWORD);
  }

  @PostMapping
  public ModelAndView requestPasswordReset(@Valid @ModelAttribute ForgotPasswordRequest forgotPasswordRequest, BindingResult bindingResult) {
    // show forgot password form if there are validation errors
    if (bindingResult.hasErrors()) {
      return new ModelAndView(ViewNames.Authentication.FORGOT_PASSWORD, HttpStatus.BAD_REQUEST);
    }

    Optional<UserDto> userDto = userService.getUserByEmailOrUsername(forgotPasswordRequest.getEmailOrUsername());

    if (userDto.isEmpty()) {
      bindingResult.rejectValue("emailOrUsername", "UserDoesNotExist");
      return new ModelAndView(ViewNames.Authentication.FORGOT_PASSWORD, HttpStatus.BAD_REQUEST);
    }

    eventPublisher.publishEvent(new OnResetPasswordRequestCompleteEvent(this, userDto.get()));

    Map<String, Object> viewModel = new HashMap<>();
    viewModel.put("isSuccessful", true);
    viewModel.put("forgotPasswordRequest", new ForgotPasswordRequest()); // to clear the form

    return new ModelAndView(ViewNames.Authentication.FORGOT_PASSWORD, viewModel, HttpStatus.OK);
  }
}
