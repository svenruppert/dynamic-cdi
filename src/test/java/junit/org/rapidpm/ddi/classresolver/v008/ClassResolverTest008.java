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
package junit.org.rapidpm.ddi.classresolver.v008;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.Produces;
import org.rapidpm.ddi.producer.Producer;

import javax.inject.Inject;

public class ClassResolverTest008 extends DDIBaseTest {

  /**
   * no Anonymous Class inside the create() Method.. compare to ClassResolverTest007
   *
   */
  @Test
  public void testProxy001() {
    final BusinessModulVirtual instance = new BusinessModulVirtual();
    Assertions.assertNotNull(instance);
    Assertions.assertNull(instance.service);
    DI.activateDI(instance);

    Assertions.assertNotNull(instance.service);
//    Assertions.assertTrue(java.lang.reflect.Proxy.isProxyClass(instance.service.getClass()));

    final String hello = instance.service.doWork("Hello");
    Assertions.assertNotNull(hello);
    Assertions.assertEquals("created by Producer", hello);


  }


  public interface Service {
    String doWork(String str);
  }

  public static class BusinessModulVirtual {
    @Inject Service service;

    public String work(String str) {
      return service.doWork(str);
    }
  }

  public static class ServiceA implements Service {

    public ServiceA() {
      System.out.println(" ServiceA = constructed...");
    }

    @Override
    public String doWork(final String str) {
      return "ServiceA_" + str;
    }
  }


  @Produces(Service.class)
  public static class ServiceProducer implements Producer<Service> {
    @Override
    public Service create() {
      return str -> "created by Producer";
    }
  }
}
