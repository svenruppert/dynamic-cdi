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
package junit.com.svenruppert.ddi.classresolver.v010;

import junit.com.svenruppert.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.svenruppert.ddi.DI;

import javax.inject.Inject;
import java.util.Set;

public class ClassResolver010Test
    extends DDIBaseTest {

  @Inject Service service;

  @Test
  public void test001() {
    final Set<Class<? extends Service>> subTypesOf = DI.getSubTypesOf(Service.class);
    for (Class<? extends Service> aClass : subTypesOf) {
      System.out.println("aClass = " + aClass);
      System.out.println("aClass.isInterface() = " + aClass.isInterface());
    }
    //DI.activateDI(this);
    Assertions.assertNotNull(service);

  }

  public interface Service {
    String doWork(String str);
  }

  public interface ServiceA extends Service {
    String doWork(String str);
  }

  public interface ServiceB extends Service {
    String doWork(String str);
  }

  public interface ServiceAA extends ServiceA {
    String doWork(String str);
  }

  public static class ServiceImpl implements ServiceAA {

    @Override
    public String doWork(final String str) {
      return "ServiceImpl " + str;
    }
  }


}
