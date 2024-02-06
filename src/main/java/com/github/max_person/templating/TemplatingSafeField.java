package com.github.max_person.templating;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation denoting a field (or a getter) as a safe-for-access field from within the {@link Template}s
 * <p>
 * Used alongside {@link TemplatingSafeMethod} to restrict the user's access to the code functionality.
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TemplatingSafeField {
    String value() default "";
}
