package io.doeasy.xcar.validator;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Constraint(validatedBy = IntervalValidator.class)
public @interface Interval {

    String message() default "interval 必须传递 7 或者 30";

    String value() default "7";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
