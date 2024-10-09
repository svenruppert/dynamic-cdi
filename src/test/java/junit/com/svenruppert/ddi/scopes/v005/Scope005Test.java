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
package junit.com.svenruppert.ddi.scopes.v005;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.svenruppert.ddi.DI;
import com.svenruppert.ddi.scopes.InjectionScopeManager;
import com.svenruppert.ddi.scopes.provided.JVMSingletonInjectionScope;

/**
 * Copyright (C) 2010 RapidPM
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created by RapidPM - Team on 02.08.16.
 */
public class Scope005Test {

  @Test
  public void test001() {
    DI.registerClassForScope(Service.class, JVMSingletonInjectionScope.class.getSimpleName());

    final SingletonTestClass instance = new SingletonTestClass();
    InjectionScopeManager.manageInstance(Service.class, instance);

    try {
      InjectionScopeManager.manageInstance(Service.class, instance);
      Assertions.fail("too bad..");
    } catch (RuntimeException e) {
      Assertions.assertTrue(e.toString().contains("tried to set the Singleton twice"));
    }
  }

  public interface Service {
  }


  public static class SingletonTestClass implements Service {
  }

}
