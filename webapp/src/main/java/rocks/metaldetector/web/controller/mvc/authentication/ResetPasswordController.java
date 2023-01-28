package rocks.metaldetector.web.controller.mvc.authentication;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.support.JwtsSupport;
import rocks.metaldetector.web.api.auth.ChangePasswordRequest;

import java.util.Date;

import static org.springframework.http.HttpStatus.OK;
import static rocks.metaldetector.support.Endpoints.Authentication.FORGOT_PASSWORD;
import static rocks.metaldetector.support.Endpoints.Authentication.LOGIN;

@Controller
@RequestMapping(Endpoints.Authentication.RESET_PASSWORD)
@AllArgsConstructor
public class ResetPasswordController {

  static final String FORM_DTO = "changePasswordRequest";

  private final UserService userService;
  private final JwtsSupport jwtsSupport;

  @GetMapping
  public ModelAndView showResetPasswordForm(@RequestParam(value = "token") String tokenString, Model model, RedirectAttributes redirectAttributes) {
    var claims = jwtsSupport.getClaims(tokenString);

    if (claims.getExpiration().before(new Date())) {
      redirectAttributes.addFlashAttribute("tokenExpiredError", true);
      return new ModelAndView("redirect:" + FORGOT_PASSWORD);
    }
    // The model may contain a form request dto with validation messages from previous request (see resetPassword() method).
    // To display the errors correctly in the HTML file, this attribute must continue to be used and must not be overwritten by a new attribute.
    if (!model.asMap().containsKey(FORM_DTO)) {
      // create new ChangePasswordRequest if model has no attribute
      model.addAttribute(ChangePasswordRequest.builder().tokenString(tokenString).build());
    }

    return new ModelAndView(ViewNames.Authentication.RESET_PASSWORD, OK);
  }

  @PostMapping
  public ModelAndView resetPassword(@Valid @ModelAttribute ChangePasswordRequest changePasswordRequest, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
    // show reset password form if there are validation errors
    if (bindingResult.hasErrors()) {
      redirectAttributes.addFlashAttribute(BindingResult.class.getName() + "." + FORM_DTO, bindingResult);
      redirectAttributes.addFlashAttribute(FORM_DTO, changePasswordRequest);
      return new ModelAndView("redirect:" + Endpoints.Authentication.RESET_PASSWORD + "?token=" + changePasswordRequest.getTokenString());
    }

    userService.resetPasswordWithToken(changePasswordRequest.getTokenString(), changePasswordRequest.getNewPlainPassword());

    return new ModelAndView("redirect:" + LOGIN + "?resetSuccess");
  }
}
