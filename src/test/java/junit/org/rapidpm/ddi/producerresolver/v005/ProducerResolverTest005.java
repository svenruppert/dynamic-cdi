package junit.org.rapidpm.ddi.producerresolver.v005;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.Produces;
import org.rapidpm.ddi.ResponsibleFor;
import org.rapidpm.ddi.producer.Producer;
import org.rapidpm.ddi.producerresolver.ProducerResolver;

import javax.inject.Inject;

/**
 * Created by Sven Ruppert on 25.01.16.
 */
public class ProducerResolverTest005 extends DDIBaseTest {


  @Inject MyService myService;

  @Test(expected = DDIModelException.class)
  public void test001() throws Exception {
    try {
      DI.activateDI(this);
    } catch (Exception e) {
      Assert.assertTrue(e instanceof DDIModelException);
      throw e;
    }
  }

  public interface MyService {
    String doWork(String txt);
  }

  @Produces(MyService.class)
  public static class Producer_A1 implements Producer<MyService> {


    @Override
    public MyService create() {
      return null;
    }
  }

  @Produces(MyService.class)
  public static class Producer_A2 implements Producer<MyService> {

    @Override
    public MyService create() {
      return null;
    }
  }

  @ResponsibleFor(MyService.class)
  public static class MyProducerResolver_A implements ProducerResolver<MyService, Producer<MyService>> {

    //no default constructor
    public MyProducerResolver_A(String txt) {
    }

    @Override
    public Class resolve(final Class<? extends MyService> interf) {
      return null;
    }
  }
}