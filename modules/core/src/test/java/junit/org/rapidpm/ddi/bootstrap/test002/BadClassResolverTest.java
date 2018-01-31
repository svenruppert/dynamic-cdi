package junit.org.rapidpm.ddi.bootstrap.test002;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.implresolver.ClassResolver;
import junit.org.rapidpm.ddi.DDIBaseTest;

/**
 *
 */
public class BadClassResolverTest extends DDIBaseTest {

  public static class DemoClass {}

  public static class BadClassResolver implements ClassResolver<DemoClass> {
    @Override
    public Class<? extends DemoClass> resolve(Class<DemoClass> interf) {
      return DemoClass.class;
    }
  }

  @Test()
  public void test001() throws Exception {
    Assertions.assertThrows(DDIModelException.class, DI::checkActiveModel);
  }
}
