package junit.org.rapidpm.ddi.classresolver.v017;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rapidpm.ddi.DI;

/**
 *
 */
public class ClassresolverTest017 {


  public static interface Service {}

  public static abstract class AbstractService implements Service {}

  public static class ServiceImpl extends AbstractService {}


  @BeforeEach
  void setUp() {
    DI.clearReflectionModel();
    DI.activatePackages("org.rapidpm");
    DI.activatePackages(this.getClass());
  }

  @AfterEach
  void tearDown() {
    DI.clearReflectionModel();
  }

  @Test
  void test001() {
    final Service service = DI.activateDI(Service.class);
    Assertions.assertEquals(service.getClass() , ServiceImpl.class);
  }

  @Test
  void test002() {
    final Service service = DI.activateDI(ServiceImpl.class);
    Assertions.assertEquals(service.getClass() , ServiceImpl.class);
  }


}
