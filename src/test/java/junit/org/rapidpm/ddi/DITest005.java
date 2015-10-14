package junit.org.rapidpm.ddi;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.ResponsibleFor;
import org.rapidpm.ddi.implresolver.ClassResolver;

import javax.inject.Inject;

/**
 * Created by svenruppert on 14.10.15.
 */
public class DITest005 {

  @Inject Service service;

  @Before
  public void setUp() throws Exception {
    DI.activateDI(this);
  }

  @Test
  public void test001() throws Exception {
    Assert.assertNotNull(service);
    Assert.assertEquals(service.getClass(), ServiceImpl_A.class);
  }

  public interface Service {
    String doWork(String txt);
  }

  public static class ServiceImpl_A implements Service {
    @Override
    public String doWork(final String txt) {
      return "Nase - " + txt;
    }
  }

}
