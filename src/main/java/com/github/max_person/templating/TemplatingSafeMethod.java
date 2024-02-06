package com.github.max_person.templating;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation denoting a method as safe-for-access from within the {@link Template}s
 * <p>
 * Used alongside {@link TemplatingSafeField} to restrict the user's access to the code functionality.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TemplatingSafeMethod {
    String value() default "";
}
