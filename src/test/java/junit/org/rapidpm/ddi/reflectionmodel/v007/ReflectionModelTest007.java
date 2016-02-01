/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package junit.org.rapidpm.ddi.reflectionmodel.v007;

import junit.org.rapidpm.ddi.reflectionmodel.v007.pkg.PkgServiceA;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.reflections.ReflectionsModel;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;

public class ReflectionModelTest007 {


  @Before
  public void setUp() throws Exception {
    DI.clearReflectionModel();
    DI.activatePackages(this.getClass());
  }


  @After
  public void tearDown() throws Exception {
    DI.clearReflectionModel();
  }


  @Test
  public void test001() throws Exception {
    final Field declaredField = DI.class.getDeclaredField("reflectionsModel");

    declaredField.setAccessible(true);
    final ReflectionsModel reflectionModel = (ReflectionsModel) declaredField.get(null);
    final Collection<String> classesForPkg = reflectionModel.getClassesForPkg(PkgServiceA.class.getPackage().getName());
    Assert.assertFalse(classesForPkg.isEmpty());
    Assert.assertEquals(2, classesForPkg.size());


    final Set<String> activatedPkgs = reflectionModel.getActivatedPkgs();
    Assert.assertFalse(activatedPkgs.isEmpty());
    Assert.assertEquals(2, activatedPkgs.size());

  }
}
