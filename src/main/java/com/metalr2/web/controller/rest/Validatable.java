package com.metalr2.web.controller.rest;

import com.metalr2.model.exceptions.ErrorMessages;
import com.metalr2.model.exceptions.ValidationException;
import org.springframework.validation.BindingResult;

public interface Validatable {

  // // TODO: 11.01.20 Delete after merge?

  default void validateRequest(BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new ValidationException(ErrorMessages.VALIDATION_ERROR.toDisplayString(), bindingResult.getFieldErrors());
    }
  }

}
