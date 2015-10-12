package junit.org.rapidpm.ddi.producer;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.Produces;
import org.rapidpm.ddi.producer.Producer;

import javax.inject.Inject;

/**
 * Created by svenruppert on 12.10.15.
 */
public class ProducerTest004 extends DDIBaseTest {


  @Inject Service service;


  @Test
  public void test001() throws Exception {
    DI.activateDI(this);


  }

  public interface Service{}

  public static class ServiceImpl implements Service{}

  @Produces(Service.class)
  public static class ServiceProducer implements Producer<Service>{
    @Override
    public Service create() {
      return new ServiceImpl();
    }
  }

  @Produces(ServiceImpl.class)
  public static class ServiceImplProducer implements Producer<Service>{
    @Override
    public Service create() {
       throw new RuntimeException("wrong producer activated");
    }
  }


}
