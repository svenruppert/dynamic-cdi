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
package junit.org.rapidpm.ddi.reflectionmodel.v007;

import junit.org.rapidpm.ddi.reflectionmodel.v007.pkg.PkgServiceA;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.reflections.ReflectionsModel;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;

public class ReflectionModelTest007 {


  @BeforeEach
  public void setUp() {
    DI.clearReflectionModel();
    DI.activatePackages(this.getClass());
  }


  @AfterEach
  public void tearDown() {
    DI.clearReflectionModel();
  }


  @Test
  public void test001() throws Exception {
    final Field declaredField = DI.class.getDeclaredField("reflectionsModel");

    declaredField.setAccessible(true);
    final ReflectionsModel reflectionModel = (ReflectionsModel) declaredField.get(null);
    final Collection<String> classesForPkg = reflectionModel.getClassesForPkg(PkgServiceA.class.getPackage().getName());
    Assertions.assertFalse(classesForPkg.isEmpty());
    Assertions.assertEquals(2, classesForPkg.size());


    final Set<String> activatedPkgs = reflectionModel.getActivatedPkgs();
    Assertions.assertFalse(activatedPkgs.isEmpty());
    Assertions.assertEquals(2, activatedPkgs.size());

  }
}
