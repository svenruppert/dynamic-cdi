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
package junit.org.rapidpm.ddi.classresolver.v007;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.DI;
import junit.org.rapidpm.ddi.DDIBaseTest;

public class ClassResolverTest007 extends DDIBaseTest {


  /**
   * Anonymous Class inside the create() Method.. will be removed
   * <p>
   * 1 Interface , 1 Impl -> Impl ServiceA will be used
   *
   */
  @Test()
  public void testProxy001() {
    final BusinessModulVirtual instance = new BusinessModulVirtual();
    Assertions.assertNotNull(instance);
    Assertions.assertNull(instance.service);
    Assertions.assertThrows(DDIModelException.class, ()->DI.activateDI(instance));
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

  public static class BusinessModul {
    private final Service service = new Service() { //this is a implementation of the Interface...
      @Override
      public String doWork(final String str) {
        return "created by Anonymous";
      }
    };

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

}
