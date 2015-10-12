package junit.org.rapidpm.ddi.producer;

import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.Produces;
import org.rapidpm.ddi.producer.Producer;

import javax.inject.Inject;

/**
 * Created by svenruppert on 12.10.15.
 */
public class ProducerTest007 {



  @Inject Service service;

  @Test
  public void test001() throws Exception {
    DI.activateDI(this);
    Assert.assertEquals(service.getClass(), ServiceImpl_B.class);
  }

  public interface Service{}

  public static class ServiceImpl_B implements Service{}

  @Produces(ServiceImpl_B.class)
  public static class ServiceImpl_B_Producer implements Producer<Service> {
    @Override
    public Service create() {
     return  new ServiceImpl_B();
    }
  }



}
