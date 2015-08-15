package junit.org.rapidpm.ddi.named;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Created by svenruppert on 14.07.15.
 */
public class NamedTest003 extends DDIBaseTest {


  @Test
  public void testInjection001() throws Exception {
    BusinessModule businessModule = new BusinessModule();

    DI.activateDI(businessModule);
    Assert.assertNotNull(businessModule.service);
    Assert.assertTrue(businessModule.service.isPostconstructed());

    Assert.assertNotNull(businessModule.service.getSubService());
    Assert.assertTrue(businessModule.service.getSubService().postconstructed);

    Assert.assertEquals("SubSubService test", businessModule.work("test"));
  }



  public static class BusinessModule {
    @Inject Service service;
    public String work(String txt) {
      return service.work(txt);
    }
  }


  public interface Service {
    String work(String txt);
    SubService getSubService();
    boolean isPostconstructed();
  }

  public static class ServiceImplA implements Service {
    @Inject SubService subService;

    public String work(String txt) {
      return subService.work(txt);
    }

    @Override
    public SubService getSubService() {
      return subService;
    }

    boolean postconstructed = false;

    public boolean isPostconstructed() {
      return postconstructed;
    }

    @PostConstruct
    public void postconstruct() {
      postconstructed = true;
    }
  }

  public static class SubService {
    @Inject SubSubService subSubService;

    public String work(final String txt) {
      return subSubService.work(txt);
    }

    boolean postconstructed = false;

    public boolean isPostconstructed() {
      return postconstructed;
    }

    @PostConstruct
    public void postconstruct() {
      postconstructed = true;
    }
  }

  public static class SubSubService {
    public String work(final String txt) {
      return "SubSubService " + txt;
    }
  }
}
