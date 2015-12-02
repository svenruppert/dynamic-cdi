package junit.org.rapidpm.ddi.producer.v008;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.Test;
import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.Produces;
import org.rapidpm.ddi.producer.Producer;

import javax.inject.Inject;

/**
 * Created by b.bosch on 02.12.2015.
 */
public class ProducerTest008 extends DDIBaseTest {


  @Test(expected = DDIModelException.class)
  public void test001() throws Exception {
    Service service = new Service();
    DI.activateDI(service);

  }

  public class Service{
    @Inject SubService subService;
  }

  public interface SubService {}

  @Produces(SubService.class)
  public class ServiceProducer implements Producer<SubService>{

    @Override
    public SubService create() {
      throw new RuntimeException("something bad happened");
    }
  }
}
