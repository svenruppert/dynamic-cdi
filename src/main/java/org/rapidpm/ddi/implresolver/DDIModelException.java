package org.rapidpm.ddi.implresolver;

/**
 * Created by svenruppert on 14.07.15.
 */
public class DDIModelException extends RuntimeException {
  public DDIModelException() {
  }

  public DDIModelException(final String message) {
    super(message);
  }

  public DDIModelException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public DDIModelException(final Throwable cause) {
    super(cause);
  }

  public DDIModelException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
