package junit.org.rapidpm.ddi.producerresolver.v004;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.Produces;
import org.rapidpm.ddi.producer.Producer;
import org.rapidpm.ddi.producerresolver.ProducerResolver;

import javax.inject.Inject;

/**
 * Created by svenruppert on 12.10.15.
 */
public class ProducerResolverTest004 extends DDIBaseTest{


  public interface MyService {
    String doWork(String txt);
  }



  @Produces(MyService.class)
  public static class Producer_A_1 implements Producer<MyService> {
    @Override
    public MyService create() {
      return null;
    }
  }

  @Produces(MyService.class)
  public static class Producer_A_2 implements Producer<MyService> {
    @Override
    public MyService create() {
      return null;
    }
  }

//  @ResponsibleFor(MyService.class)
  public static class MyProducerResolver_A implements ProducerResolver<MyService,Producer<MyService>> {
    @Override
    public Class resolve(final Class<? extends MyService> interf) {
      return Producer_A_2.class;
    }
  }




  @Inject MyService myService;

  @Test(expected = DDIModelException.class)
  public void test001() throws Exception {
    try {
      DI.activateDI(this);
    } catch (Exception e) {
      Assert.assertTrue(e instanceof DDIModelException);
      Assert.assertTrue(e.getMessage().contains("ProducerResolver without ResponsibleFor"));
      throw e;
    }
  }
}
