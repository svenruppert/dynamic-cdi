package org.rapidpm.ddi.implresolver;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by svenruppert on 05.08.15.
 */


@Target({TYPE})
@Retention(RUNTIME)
@Documented
public @interface ResponsibleForInterface {
  Class value();
}
