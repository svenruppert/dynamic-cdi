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
package junit.org.rapidpm.ddi.classresolver.v005;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.ResponsibleFor;
import org.rapidpm.ddi.implresolver.ClassResolver;

import javax.inject.Inject;

public class ClassResolverTest005 extends DDIBaseTest {


  @Test()
  public void test001() {
    try {
      DI.checkActiveModel();
      Assertions.fail("too bad..");
    } catch (DDIModelException e) {
      final String message = e.getMessage();
      Assertions.assertTrue(message.contains("Found ClassResolver without @ResponsibleFor annotation"));
    }

  }

  @Test
  public void test002() {
    final BusinessModule businessModule = new BusinessModule();
    DI.activateDI(businessModule);

    Assertions.assertEquals(ServiceImplA.class, businessModule.service.getClass());

  }

  public interface Service {
    String work(String txt);
  }

  @ResponsibleFor(Service.class)
  public static class ServiceClassResolverA implements ClassResolver<Service> {
    @Override
    public Class<? extends Service> resolve(final Class<Service> interf) {
      return ServiceImplA.class;
    }
  }

  public static class ServiceClassResolverB implements ClassResolver<Service> {
    @Override
    public Class<? extends Service> resolve(final Class<Service> interf) {
      return null;
    }
  }

  public static class BusinessModule {
    @Inject Service service;

    public String work(String txt) {
      return service.work(txt);
    }
  }

  public static class ServiceImplA implements Service {
    public String work(String txt) {
      return txt;
    }
  }

  public static class ServiceImplB implements Service {
    public String work(String txt) {
      return txt;
    }

  }
}
