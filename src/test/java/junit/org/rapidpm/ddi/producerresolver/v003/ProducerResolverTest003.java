package junit.org.rapidpm.ddi.producerresolver.v003;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.Produces;
import org.rapidpm.ddi.ResponsibleFor;
import org.rapidpm.ddi.implresolver.ClassResolver;
import org.rapidpm.ddi.producer.Producer;
import org.rapidpm.ddi.producerresolver.ProducerResolver;

import javax.inject.Inject;

/**
 * Created by svenruppert on 25.09.15.
 */
public class ProducerResolverTest003 {

  @Before
  public void setUp() throws Exception {
    DI.activatePackages("org.rapidpm");
    DI.activatePackages(this.getClass().getPackage().getName());
  }

  @After
  public void tearDown() throws Exception {
    DI.clearReflectionModel();
  }


  public interface MyService {
    String doWork(String txt);
  }

  public static class MyServiceImpl_A implements MyService {
    @Override
    public String doWork(final String txt) {
      return this.getClass().getSimpleName() + txt;
    }
  }


  @Produces(MyServiceImpl_A.class)
  public static class Producer_A_1 implements Producer<MyServiceImpl_A> {
    @Override
    public MyServiceImpl_A create() {
      return new MyServiceImpl_A();
    }
  }

  @Produces(MyServiceImpl_A.class)
  public static class Producer_A_2 implements Producer<MyServiceImpl_A> {
    @Override
    public MyServiceImpl_A create() {
      return new MyServiceImpl_A();
    }
  }

  @ResponsibleFor(MyServiceImpl_A.class)
  public static class MyProducerResolver_A implements ProducerResolver<MyServiceImpl_A,Producer<MyServiceImpl_A>> {
    @Override
    public Class resolve(final Class<? extends MyServiceImpl_A> interf) {
      return Producer_A_2.class;
    }
  }

  @ResponsibleFor(MyServiceImpl_A.class)
  public static class MyProducerResolver_B implements ProducerResolver<MyServiceImpl_A,Producer<MyServiceImpl_A>> {
    @Override
    public Class resolve(final Class<? extends MyServiceImpl_A> interf) {
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
      Assert.assertTrue(e.getMessage().contains("toooo many ProducerResolver for interface/class class"));
      throw e;
    }
  }
}
