package com.metalr2.web.validation;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FieldsValueMatchConstraintValidator implements ConstraintValidator<FieldsValueMatch, Object> {

  private String fieldPropertyName;
  private String fieldMatchPropertyName;

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    Object fieldValue      = new BeanWrapperImpl(value).getPropertyValue(fieldPropertyName);
    Object fieldMatchValue = new BeanWrapperImpl(value).getPropertyValue(fieldMatchPropertyName);

    if (fieldValue != null) {
      return fieldValue.equals(fieldMatchValue);
    } else {
      return fieldMatchValue == null;
    }
  }

  @Override
  public void initialize(FieldsValueMatch constraintAnnotation) {
    fieldPropertyName = constraintAnnotation.field();
    fieldMatchPropertyName = constraintAnnotation.fieldMatch();
  }
}

