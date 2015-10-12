package junit.org.rapidpm.ddi.reflectionmodel.v004.model001;


import junit.org.rapidpm.ddi.reflectionmodel.v004.api.DemoAnnotation;
import junit.org.rapidpm.ddi.reflectionmodel.v004.api.Service;

/**
 * Created by svenruppert on 12.10.15.
 */
@DemoAnnotation
public class ServiceImplA implements Service {
  @Override
  public String doWork(final String txt) {
    return this.getClass().getName() + " - " + txt;
  }
}
