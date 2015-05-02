package demo.ruleengine;


import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by sven on 20.04.15.
 */
@Target({ElementType.TYPE})
@Retention(RUNTIME)
@Documented
@Inherited
public @interface Responsible {
  Class tragetInterface();
}
