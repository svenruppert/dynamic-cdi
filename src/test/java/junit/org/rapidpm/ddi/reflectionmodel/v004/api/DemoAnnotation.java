package junit.org.rapidpm.ddi.reflectionmodel.v004.api;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by svenruppert on 12.10.15.
 */
@Target({TYPE})
@Retention(RUNTIME)
@Documented
public @interface DemoAnnotation {
}
