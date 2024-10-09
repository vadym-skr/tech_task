package technikal.task.fishmarket.utils.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidFilesValidator.class)
public @interface ValidFiles {
    long maxSize() default 1024;

    String[] allowedTypes() default {};

    String message() default "Файл невалідний";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}