package junit.org.rapidpm.ddi.reflectionmodel;

import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.implresolver.DDIModelException;

import javax.inject.Inject;

/**
 * Created by svenruppert on 17.08.15.
 */
public class ReflectionModelTest001 {

  @Test(expected = DDIModelException.class)
  public void test001() throws Exception {
    DI.clearReflectionModel();
    DI.activatePackages("org.rapidpm");
    try {
      DI.activateDI(new BusinessModule());
    } catch (Exception e) {
      if (e instanceof DDIModelException) {
        final String message = e.getMessage();
        Assert.assertTrue(message.contains("only interfaces found for interface"));
        throw e;
      } else {
        Assert.fail();
      }
    }
  }


  @Test
  public void test002() throws Exception {
    DI.activatePackages("junit.org.rapidpm");
    final BusinessModule businessModule = DI.activateDI(new BusinessModule());
  }


  interface Service {
  }

  public static class ServiceImpl implements Service {
  }

  public static class BusinessModule {
    @Inject Service service;
  }


}
