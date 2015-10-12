package junit.org.rapidpm.ddi.producer;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.producer.Producer;

import javax.inject.Inject;
import org.rapidpm.ddi.Produces;

/**
 * Created by svenruppert on 07.08.15.
 */
public class ProducerTest002 extends DDIBaseTest {

  @Test(expected = DDIModelException.class)
  public void testProducer001() throws Exception {
    final BusinessModul businessModul = new BusinessModul();
    try {
      DI.activateDI(businessModul);
    } catch (Exception e) {
      final String msg = e.getMessage();
      System.out.println("msg = " + msg);
      Assert.assertTrue(msg.contains("to many Producer and no ProducerResolver found for interface"));
      throw e;
    }

  }


  public interface Service {
    String workOn(String txt);
  }

  @Produces(Service.class)
  public static class ServiceProducer01 implements Producer<Service> {
    public Service create() {
      return txt -> txt + "_" + ServiceProducer01.class.getSimpleName();
    }
  }

  @Produces(Service.class)
  public static class ServiceProducer02 implements Producer<Service> {
    public Service create() {
      return txt -> txt + "_" + ServiceProducer01.class.getSimpleName();
    }
  }

  public static class BusinessModul {
    @Inject Service service;

    public String doIt(String txt) {
      return service.workOn(txt);
    }
  }

}
