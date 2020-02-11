package rocks.metaldetector.web.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FieldsValueMatchConstraintValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldsValueMatch {

  String message() default "fields values must match";

  String field();

  String fieldMatch();

  @Target({ ElementType.TYPE })
  @Retention(RetentionPolicy.RUNTIME)
  @interface List {
    FieldsValueMatch[] value();
  }

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
