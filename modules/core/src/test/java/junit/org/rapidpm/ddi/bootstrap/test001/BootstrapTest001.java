package junit.org.rapidpm.ddi.bootstrap.test001;


import junit.org.rapidpm.ddi.bootstrap.test001.api.Service;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.DI;


/**
 * Created by benjamin-bosch on 05.04.17.
 */
public class BootstrapTest001 {

  @Before
  public void setUp() throws Exception {
    DI.clearReflectionModel();
    System.clearProperty(DI.ORG_RAPIDPM_DDI_PACKAGESFILE);
  }


  @After
  public void tearDown() throws Exception {
    DI.clearReflectionModel();
    System.clearProperty(DI.ORG_RAPIDPM_DDI_PACKAGESFILE);
  }

  @Test(expected = DDIModelException.class)
  public void testInjection001() throws Exception {

    DI.activatePackages(BootstrapTest001.class);
    DI.bootstrap();
    Service service = DI.activateDI(Service.class);

    Assert.fail();
  }

  @Test
  public void test002() throws Exception {
    String path = this.getClass().getResource("good.packages").getPath();
    System.setProperty(DI.ORG_RAPIDPM_DDI_PACKAGESFILE, path);
    DI.bootstrap();
    Service service = DI.activateDI(Service.class);
    Assert.assertEquals("done", service.doWork());
  }

  @Test(expected = RuntimeException.class)
  public void test003() throws Exception {
    String path = this.getClass().getResource("bad.packages").getPath();
    System.setProperty(DI.ORG_RAPIDPM_DDI_PACKAGESFILE, path);
    DI.bootstrap();
    Service service = DI.activateDI(Service.class);
    Assert.assertEquals("done", service.doWork());
  }

  @Test(expected = DDIModelException.class)
  public void test004() throws Exception {

    System.setProperty(DI.ORG_RAPIDPM_DDI_PACKAGESFILE, "");
    DI.bootstrap();
    Service service = DI.activateDI(Service.class);
  }

  @Test(expected = DDIModelException.class)
  public void test005() throws Exception {
    System.setProperty(DI.ORG_RAPIDPM_DDI_PACKAGESFILE, "NO_FILE");
    DI.bootstrap();
    Service service = DI.activateDI(Service.class);
  }
}
