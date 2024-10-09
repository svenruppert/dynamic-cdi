/**
 * Copyright © 2013 Sven Ruppert (sven.ruppert@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package junit.com.svenruppert.ddi.scopes.v004;

import junit.com.svenruppert.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.svenruppert.ddi.DI;
import com.svenruppert.ddi.scopes.InjectionScopeManager;
import com.svenruppert.ddi.scopes.provided.JVMSingletonInjectionScope;

public class Scopes004Test
    extends DDIBaseTest {

  @Test
  public void test001() {
    final Service serviceA = DI.activateDI(Service.class);
    final Service serviceB = DI.activateDI(Service.class);

    Assertions.assertNotNull(serviceA);
    Assertions.assertNotNull(serviceB);

    Assertions.assertNotEquals(serviceA.value(), serviceB.value());
  }

  @Test
  public void test002() {
    DI.bootstrap();
    DI.registerClassForScope(SingleResource.class, JVMSingletonInjectionScope.class.getSimpleName());

    final Service serviceA = DI.activateDI(Service.class);
    final Service serviceB = DI.activateDI(Service.class);

    Assertions.assertNotNull(serviceA);
    Assertions.assertNotNull(serviceB);

    Assertions.assertEquals(serviceA.value(), serviceB.value());
  }

  @Test
  public void test003() {
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
  public void test004() {
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
