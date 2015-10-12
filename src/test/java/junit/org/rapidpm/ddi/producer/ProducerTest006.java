package junit.org.rapidpm.ddi.producer;

import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.Produces;
import org.rapidpm.ddi.producer.Producer;

import javax.inject.Inject;

/**
 * Created by svenruppert on 12.10.15.
 */
public class ProducerTest006 {



  @Inject Service service;

  @Test
  public void test001() throws Exception {
      DI.activateDI(this);
  }

  public interface Service{}

  public static class ServiceImpl_A implements Service{}
  public static class ServiceImpl_B implements Service{}

  @Produces(Service.class)
  public static class ServiceProducer_A implements Producer<Service> {
    @Override
    public Service create() {
      return new ServiceImpl_A();
    }
  }


  @Produces(ServiceImpl_A.class)
  public static class ServiceImpl_A_Producer implements Producer<Service>{
    @Override
    public Service create() {
      throw new RuntimeException("wrong producer activated");
    }
  }

  @Produces(ServiceImpl_B.class)
  public static class ServiceImpl_B_Producer implements Producer<Service>{
    @Override
    public Service create() {
      throw new RuntimeException("wrong producer activated");
    }
  }
}
