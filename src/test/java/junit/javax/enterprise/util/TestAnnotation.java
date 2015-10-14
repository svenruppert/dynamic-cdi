package junit.javax.enterprise.util;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * Created by svenruppert on 12.10.15.
 */
@Target({TYPE})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface TestAnnotation { }
