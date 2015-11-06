package org.rapidpm.ddi;

import org.rapidpm.proxybuilder.type.virtual.CreationStrategy;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Documented
public @interface Proxy {

  boolean virtual() default false;

  CreationStrategy concurrent() default CreationStrategy.NONE;

  boolean metrics() default false;

  boolean secure() default false;

  boolean logging() default false;

  ProxyType proxyType() default ProxyType.DYNAMIC;

  enum ProxyType {
    DYNAMIC,
    STATIC
  }

}
