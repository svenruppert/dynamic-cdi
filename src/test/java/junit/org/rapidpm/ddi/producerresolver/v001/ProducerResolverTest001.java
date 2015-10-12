package junit.org.rapidpm.ddi.producerresolver.v001;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.ResponsibleFor;
import org.rapidpm.ddi.producer.Producer;
import org.rapidpm.ddi.producerresolver.ProducerResolver;

import javax.inject.Inject;
import org.rapidpm.ddi.Produces;

/**
 * Created by svenruppert on 25.09.15.
 */
public class ProducerResolverTest001 extends DDIBaseTest{

  public interface MyService {
    String doWork(String txt);
  }

  public static class MyServiceImpl_A implements MyService {
    @Override
    public String doWork(final String txt) {
      return this.getClass().getSimpleName() + txt;
    }
  }

  static boolean producer_a_1 = false;
  static boolean producer_a_2 = false;

  @Produces(MyServiceImpl_A.class)
  public static class Producer_A_1 implements Producer<MyServiceImpl_A> {
    @Override
    public MyServiceImpl_A create() {
      producer_a_1 = true;
      return new MyServiceImpl_A();
    }
  }

  @Produces(MyServiceImpl_A.class)
  public static class Producer_A_2 implements Producer<MyServiceImpl_A> {
    @Override
    public MyServiceImpl_A create() {
      producer_a_2 = true;
      return new MyServiceImpl_A();
    }
  }

  @ResponsibleFor(MyServiceImpl_A.class)
  public static class MyProducerResolver implements ProducerResolver<MyServiceImpl_A,Producer<MyServiceImpl_A>> {
    @Override
    public Class resolve(final Class<? extends MyServiceImpl_A> interf) {
      return Producer_A_2.class;
    }
  }


  @Inject MyService myService;

  @Test
  public void test001() throws Exception {
    DI.activateDI(this);
    Assert.assertNotNull(myService);
    final String s = "Hello";
    Assert.assertEquals(MyServiceImpl_A.class.getSimpleName() + s,myService.doWork(s) );
    Assert.assertFalse(producer_a_1);
    Assert.assertTrue(producer_a_2);

  }
}
