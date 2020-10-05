package edu.wctc.registration.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = WctcEmailValidator.class)
@Documented
public @interface WctcEmail {
    Class<?>[] groups() default {};

    String message() default "Must use a WCTC email address";

    Class<? extends Payload>[] payload() default {};
}
