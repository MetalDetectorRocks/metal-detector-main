package com.metalr2.web.controller.mvc.authentication;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.MessageKeys;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.model.token.JwtsSupport;
import com.metalr2.model.token.TokenEntity;
import com.metalr2.model.user.UserEntity;
import com.metalr2.service.token.TokenService;
import com.metalr2.service.user.UserService;
import com.metalr2.web.dto.request.ChangePasswordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
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

import javax.validation.Valid;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping(Endpoints.Guest.RESET_PASSWORD)
public class ResetPasswordController {

  static final String FORM_DTO = "changePasswordRequest";

  private final UserService userService;
  private final TokenService tokenService;
  private final MessageSource messages;
  private final JwtsSupport jwtsSupport;

  @Autowired
  public ResetPasswordController(UserService userService, TokenService tokenService,
                                 @Qualifier("messageSource") MessageSource messages, JwtsSupport jwtsSupport) {
    this.userService = userService;
    this.tokenService = tokenService;
    this.messages = messages;
    this.jwtsSupport = jwtsSupport;
  }

  @GetMapping
  public ModelAndView showResetPasswordForm(@RequestParam(value = "token") String tokenString, Model model, RedirectAttributes redirectAttributes) {
    Optional<TokenEntity> tokenEntity = tokenService.getResetPasswordTokenByTokenString(tokenString);

    // check whether token exists
    if (tokenEntity.isEmpty()) {
      redirectAttributes.addFlashAttribute("resetPasswordError", messages.getMessage(MessageKeys.ForgotPassword.TOKEN_DOES_NOT_EXIST, null, Locale.US));
      return new ModelAndView("redirect:" + Endpoints.Guest.FORGOT_PASSWORD);
    }
    // check whether token is expired
    else if (tokenEntity.get().isExpired()) {
      redirectAttributes.addFlashAttribute("resetPasswordError", messages.getMessage(MessageKeys.ForgotPassword.TOKEN_IS_EXPIRED, null, Locale.US));
      return new ModelAndView("redirect:" + Endpoints.Guest.FORGOT_PASSWORD);
    }
    // everything is OK here, set token as hidden input field
    else {
      // The model may contain a form request dto with validation messages from previous request (see resetPassword() method).
      // To display the errors correctly in the HTML file, this attribute must continue to be used and must not be overwritten by a new attribute.
      if (!model.asMap().containsKey(FORM_DTO)) {
        // create new ChangePasswordRequest if model has no attribute
        model.addAttribute(ChangePasswordRequest.builder().tokenString(tokenString).build());
      }

      return new ModelAndView(ViewNames.Guest.RESET_PASSWORD, HttpStatus.OK);
    }
  }

  @PostMapping
  public ModelAndView resetPassword(@Valid @ModelAttribute ChangePasswordRequest changePasswordRequest, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
    // show reset password form if there are validation errors
    if (bindingResult.hasErrors()) {
      redirectAttributes.addFlashAttribute(BindingResult.class.getName() + "." + FORM_DTO, bindingResult);
      redirectAttributes.addFlashAttribute(FORM_DTO, changePasswordRequest);
      return new ModelAndView("redirect:" + Endpoints.Guest.RESET_PASSWORD + "?token=" + changePasswordRequest.getTokenString());
    }

    changePassword(changePasswordRequest);

    return new ModelAndView("redirect:" + Endpoints.Guest.LOGIN + "?resetSuccess");
  }

  private void changePassword(ChangePasswordRequest changePasswordRequest) {
    // 1. get claims to check signature of token
    jwtsSupport.getClaims(changePasswordRequest.getTokenString());

    // 2. get user from token
    Optional<TokenEntity> tokenEntity = tokenService.getResetPasswordTokenByTokenString(changePasswordRequest.getTokenString());
    @SuppressWarnings("OptionalGetWithoutIsPresent") // safe here, because we have validation in method showResetPasswordForm()
    UserEntity userEntity = tokenEntity.get().getUser();

    // 3. set new password
    userService.changePassword(userEntity, changePasswordRequest.getNewPlainPassword());

    // 4. remove token from database
    tokenService.deleteToken(tokenEntity.get());
  }
}
