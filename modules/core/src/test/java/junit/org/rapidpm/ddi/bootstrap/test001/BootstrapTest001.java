package junit.org.rapidpm.ddi.bootstrap.test001;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.DI;
import junit.org.rapidpm.ddi.bootstrap.test001.api.Service;


/**
 * Created by benjamin-bosch on 05.04.17.
 */
public class BootstrapTest001 {

  @BeforeEach
  public void setUp() throws Exception {
    DI.clearReflectionModel();
    System.clearProperty(DI.ORG_RAPIDPM_DDI_PACKAGESFILE);
  }


  @AfterEach
  public void tearDown() throws Exception {
    DI.clearReflectionModel();
    System.clearProperty(DI.ORG_RAPIDPM_DDI_PACKAGESFILE);
  }

  @Test()
  public void testInjection001() throws Exception {

    DI.activatePackages(BootstrapTest001.class);
    DI.bootstrap();
    Assertions.assertThrows(DDIModelException.class , () -> DI.activateDI(Service.class));

  }

  @Test
  public void test002() throws Exception {
    String path = this.getClass().getResource("good.packages").getPath();
    System.setProperty(DI.ORG_RAPIDPM_DDI_PACKAGESFILE , path);
    DI.bootstrap();
    Service service = DI.activateDI(Service.class);
    Assertions.assertEquals("done" , service.doWork());
  }

  @Test()
  public void test003() throws Exception {
    String path = this.getClass().getResource("bad.packages").getPath();
    System.setProperty(DI.ORG_RAPIDPM_DDI_PACKAGESFILE , path);
    DI.bootstrap();
    final Service service = DI.activateDI(Service.class);
    Assertions.assertThrows(RuntimeException.class , service::doWork);
  }

  @Test()
  public void test004() throws Exception {
    System.setProperty(DI.ORG_RAPIDPM_DDI_PACKAGESFILE , "");
    DI.bootstrap();
    Assertions.assertThrows(DDIModelException.class , () -> DI.activateDI(Service.class));

  }

  @Test()
  public void test005() throws Exception {
    System.setProperty(DI.ORG_RAPIDPM_DDI_PACKAGESFILE , "NO_FILE");
    Assertions.assertThrows(DDIModelException.class , DI::bootstrap);
  }

  @Test
  public void test006() throws Exception {
    System.setProperty(DI.ORG_RAPIDPM_DDI_PACKAGESFILE , BootstrapTest001.class.getPackage().getName().replace("." , "/") + "/good.packages");
    DI.bootstrap();
    Service service = DI.activateDI(Service.class);
    Assertions.assertEquals("done" , service.doWork());
  }
}
