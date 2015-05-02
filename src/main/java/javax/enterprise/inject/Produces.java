package javax.enterprise.inject;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by sven on 02.05.15.
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
@Documented
public @interface Produces {
}
