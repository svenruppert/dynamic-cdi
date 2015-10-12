package junit.org.rapidpm.ddi.reflectionmodel.v004.model002;

import junit.org.rapidpm.ddi.reflectionmodel.v004.model001.ServiceImplA;

/**
 * Created by svenruppert on 12.10.15.
 */
public class ServiceImplB extends ServiceImplA {
  @Override
  public String doWork(final String txt) {
    return this.getClass().getName() + " - " + txt;
  }
}
