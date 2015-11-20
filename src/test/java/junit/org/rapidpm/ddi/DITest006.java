package junit.org.rapidpm.ddi;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rapidpm.ddi.DI;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Created by svenruppert on 17.11.15.
 */
public class DITest006 {
  static boolean postconstructA1 = false;
  static boolean postconstructA2 = false;
  static boolean postconstructB1 = false;
  static boolean postconstructB2 = false;

  @Before
  public void setUp() throws Exception {
    DI.clearReflectionModel();
    DI.activatePackages(DITest006.class.getPackage().getName());
  }

  @Test
  public void test001() throws Exception {
    Assert.assertFalse(postconstructA1);
    Assert.assertFalse(postconstructA2);
    Assert.assertFalse(postconstructB1);
    Assert.assertFalse(postconstructB2);

    final BusinessModule businessModule = new BusinessModule();
    DI.activateDI(businessModule);

    Assert.assertTrue(postconstructA1);
    Assert.assertTrue(postconstructA2);
    Assert.assertTrue(postconstructB1);
    Assert.assertTrue(postconstructB2);


  }

  public interface Service {
    String doWork(String txt);
  }

  public static class ServiceImpl implements Service {
    @Override
    public String doWork(final String txt) {
      return this.getClass().getSimpleName() + " " + txt;
    }

    @PostConstruct
    public void post001() {
      if (postconstructA1 == true) Assert.fail();
      postconstructA1 = true;
    }

    @PostConstruct
    public void post002() {
      if (postconstructA2 == true) Assert.fail();
      postconstructA2 = true;
    }
  }

  public static class BusinessModule {
    @Inject Service service;

    @PostConstruct
    public void post001() {
      if (postconstructB1 == true) Assert.fail();
      postconstructB1 = true;
    }

    @PostConstruct
    public void post002() {
      if (postconstructB2 == true) Assert.fail();
      postconstructB2 = true;
    }

  }


}

