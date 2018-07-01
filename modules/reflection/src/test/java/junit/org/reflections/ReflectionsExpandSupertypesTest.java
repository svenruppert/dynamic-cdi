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
package junit.org.reflections;

import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReflectionsExpandSupertypesTest {

  private final static String        packagePrefix =
      "junit.org.reflections.ReflectionsExpandSupertypesTest\\$TestModel\\$ScannedScope\\$.*";
  private              FilterBuilder inputsFilter  = new FilterBuilder().include(packagePrefix);

  @Test
  public void testExpandSupertypes() throws Exception {
    Reflections refExpand = new Reflections(new ConfigurationBuilder().
        setUrls(ClasspathHelper.forClass(TestModel.ScannedScope.C.class)).
        filterInputsBy(inputsFilter));
    assertTrue(refExpand.getConfiguration().shouldExpandSuperTypes());
    Set<Class<? extends TestModel.A>> subTypesOf = refExpand.getSubTypesOf(TestModel.A.class);
    assertTrue(subTypesOf.contains(TestModel.B.class));
    assertTrue(subTypesOf.containsAll(refExpand.getSubTypesOf(TestModel.B.class)));
  }

  @Test
  public void testNotExpandSupertypes() throws Exception {
    Reflections refDontExpand = new Reflections(new ConfigurationBuilder().
        setUrls(ClasspathHelper.forClass(TestModel.ScannedScope.C.class)).
        filterInputsBy(inputsFilter).
        setExpandSuperTypes(false));
    assertFalse(refDontExpand.getConfiguration().shouldExpandSuperTypes());
    Set<Class<? extends TestModel.A>> subTypesOf1 = refDontExpand.getSubTypesOf(TestModel.A.class);
    assertFalse(subTypesOf1.contains(TestModel.B.class));
  }

  public interface TestModel {
    interface A {
    } // outside of scanned scope

    interface B extends A {
    } // outside of scanned scope, but immediate supertype

    interface ScannedScope {
      interface C extends B {
      }

      interface D extends B {
      }
    }
  }
}
