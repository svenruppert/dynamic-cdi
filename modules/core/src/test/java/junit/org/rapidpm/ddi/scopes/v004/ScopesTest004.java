package junit.org.rapidpm.ddi.scopes.v004;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.scopes.InjectionScopeManager;
import org.rapidpm.ddi.scopes.provided.JVMSingletonInjectionScope;

public class ScopesTest004 extends DDIBaseTest {

  @Test
  public void test001() throws Exception {
    final Service serviceA = DI.activateDI(Service.class);
    final Service serviceB = DI.activateDI(Service.class);

    Assertions.assertNotNull(serviceA);
    Assertions.assertNotNull(serviceB);

    Assertions.assertNotEquals(serviceA.value(), serviceB.value());
  }

  @Test
  public void test002() throws Exception {
    DI.bootstrap();
    DI.registerClassForScope(SingleResource.class, JVMSingletonInjectionScope.class.getSimpleName());

    final Service serviceA = DI.activateDI(Service.class);
    final Service serviceB = DI.activateDI(Service.class);

    Assertions.assertNotNull(serviceA);
    Assertions.assertNotNull(serviceB);

    Assertions.assertEquals(serviceA.value(), serviceB.value());
  }

  @Test
  public void test003() throws Exception {
    DI.registerClassForScope(SingleResource.class, JVMSingletonInjectionScope.class.getSimpleName());
    DI.registerClassForScope(ServiceImpl.class, JVMSingletonInjectionScope.class.getSimpleName());
    DI.registerClassForScope(Service.class, JVMSingletonInjectionScope.class.getSimpleName());

    final Service serviceA = DI.activateDI(Service.class);
    final Service serviceB = DI.activateDI(Service.class);

    Assertions.assertNotNull(serviceA);
    Assertions.assertNotNull(serviceB);

    Assertions.assertEquals(serviceA.value(), serviceB.value());
  }

  @Test
  public void test004() throws Exception {
    final String scopeBefore = InjectionScopeManager.scopeForClass(SingleResource.class);
    DI.registerClassForScope(SingleResource.class, JVMSingletonInjectionScope.class.getSimpleName());
    final String scopeAfter = InjectionScopeManager.scopeForClass(SingleResource.class);

    Assertions.assertNotEquals(scopeBefore, scopeAfter);

    final Service serviceA = DI.activateDI(Service.class);
    final Service serviceB = DI.activateDI(Service.class);

    Assertions.assertNotNull(serviceA);
    Assertions.assertNotNull(serviceB);

    Assertions.assertEquals(serviceA.value(), serviceB.value());
  }

}
