package junit.org.rapidpm.ddi.producer;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.producer.Producer;

import javax.inject.Inject;
import org.rapidpm.ddi.Produces;

/**
 * Created by svenruppert on 14.09.15.
 */
public class ProducerTest003 extends DDIBaseTest {


  @Test
  public void testProducer001() throws Exception {
    final BusinessModul businessModul = new BusinessModul();
    DI.activateDI(businessModul);

    Assert.assertNotNull(businessModul);
    Assert.assertNotNull(businessModul.service);
    Assert.assertNotNull(businessModul.service.workOn("AEAE"));
    Assert.assertEquals("AEAE_" + ServiceProducer.class.getSimpleName(), businessModul.service.workOn("AEAE"));

  }


  public interface Service<T> {
    T workOn(T txt);
  }

  @Produces(Service.class)
  public static class ServiceProducer implements Producer<Service<String>> {
    public Service<String> create() {
      return txt -> txt + "_" + ServiceProducer.class.getSimpleName();
    }
  }

  public static class BusinessModul {
    @Inject Service service;

    public String doIt(String txt) {
      return (String) service.workOn(txt);
    }
  }

}