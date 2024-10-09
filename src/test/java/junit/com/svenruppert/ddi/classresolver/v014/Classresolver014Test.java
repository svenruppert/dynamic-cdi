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
package junit.com.svenruppert.ddi.classresolver.v014;

import junit.com.svenruppert.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.svenruppert.ddi.DI;
import com.svenruppert.ddi.ResponsibleFor;
import com.svenruppert.ddi.implresolver.ClassResolver;

public class Classresolver014Test
    extends DDIBaseTest {

  public static Boolean toggle = true;

  @BeforeEach
  public void setUp() {
    DI.clearReflectionModel();
    DI.activatePackages("org.rapidpm");
    DI.activatePackages(this.getClass());
  }

  @Test
  public void test001() {
    Assertions.assertEquals(ServiceB.class, DI.activateDI(Service.class).getClass());
    Assertions.assertEquals(ServiceA.class, DI.activateDI(Service.class).getClass());
    Assertions.assertEquals(ServiceB.class, DI.activateDI(Service.class).getClass());
    DI.clearReflectionModel();
  }

  public interface Service {
    String doWork(String txt);
  }

  public static class ServiceA implements Service {
    public String doWork(String txt) {
      return txt + "A";
    }
  }

  public static class ServiceB implements Service {
    public String doWork(String txt) {
      return txt + "B";
    }
  }

  @ResponsibleFor(Service.class)
  public static class ServiceClassResolver implements ClassResolver<Service> {
    @Override
    public Class<? extends Service> resolve(final Class<Service> interf) {
      toggle = !toggle;
      System.out.println("toggle = " + toggle);
      return (toggle) ? ServiceA.class : ServiceB.class;
    }
  }
}
