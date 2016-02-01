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

package junit.org.rapidpm.ddi.reflectionmodel.v004;

import junit.org.rapidpm.ddi.reflectionmodel.v004.api.DemoAnnotation;
import junit.org.rapidpm.ddi.reflectionmodel.v004.api.Service;
import junit.org.rapidpm.ddi.reflectionmodel.v004.model001.ServiceImplA;
import junit.org.rapidpm.ddi.reflectionmodel.v004.model002.ServiceImplB;
import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;

import java.lang.annotation.Annotation;
import java.util.Set;


public class ReflectionModelTest004 {

  @Test
  public void test001_a() throws Exception {
    preCheck();

    DI.activatePackages(ServiceImplA.class.getPackage().getName());

    Assert.assertFalse(DI.getTypesAnnotatedWith(DemoAnnotation.class).isEmpty());
    Assert.assertFalse(DI.getTypesAnnotatedWith(DemoAnnotation.class,false).isEmpty());
    Assert.assertFalse(DI.getTypesAnnotatedWith(DemoAnnotation.class,true).isEmpty());
  }

  private void preCheck() {
    DI.clearReflectionModel();

    DI.activatePackages("org.rapidpm");
    DI.activatePackages(checkPkgIsNotActivated());

    Assert.assertTrue(DI.getTypesAnnotatedWith(DemoAnnotation.class).isEmpty());
  }

  private String checkPkgIsNotActivated() {
    final Package aPackage = Service.class.getPackage();
    final String aPackageName = aPackage.getName();
    Assert.assertFalse(DI.isPkgPrefixActivated(aPackageName));
    return aPackageName;
  }

  @Test
  public void test001_b() throws Exception {
    preCheck();

    DI.activatePackages(ServiceImplA.class.getPackage().getName());

    Annotation annotation = () -> DemoAnnotation.class;
    Assert.assertFalse(DI.getTypesAnnotatedWith(annotation).isEmpty());
    Assert.assertFalse(DI.getTypesAnnotatedWith(annotation,false).isEmpty());
    Assert.assertFalse(DI.getTypesAnnotatedWith(annotation,true).isEmpty());
  }

  @Test
  public void test002_a() throws Exception {
    preCheck();

    DI.activatePackages(ServiceImplB.class.getPackage().getName());

    Assert.assertTrue(DI.getTypesAnnotatedWith(DemoAnnotation.class).isEmpty());
    Assert.assertTrue(DI.getTypesAnnotatedWith(DemoAnnotation.class,false).isEmpty());
    Assert.assertTrue(DI.getTypesAnnotatedWith(DemoAnnotation.class,true).isEmpty());

    DI.activatePackages(ServiceImplA.class.getPackage().getName());
    Assert.assertFalse(DI.getTypesAnnotatedWith(DemoAnnotation.class).isEmpty());
    final Set<Class<?>> withInherit = DI.getTypesAnnotatedWith(DemoAnnotation.class, false);
    Assert.assertFalse(withInherit.isEmpty());
    Assert.assertEquals(withInherit.size(), 2);

    final Set<Class<?>> withoutInherit = DI.getTypesAnnotatedWith(DemoAnnotation.class, true);
    Assert.assertEquals(withoutInherit.size(), 1);
    Assert.assertFalse(withoutInherit.isEmpty());
  }

  @Test
  public void test002_b() throws Exception {
    preCheck();

    DI.activatePackages(ServiceImplB.class.getPackage().getName());

    Annotation annotation = () -> DemoAnnotation.class;

    Assert.assertTrue(DI.getTypesAnnotatedWith(annotation).isEmpty());
    Assert.assertTrue(DI.getTypesAnnotatedWith(annotation,false).isEmpty());
    Assert.assertTrue(DI.getTypesAnnotatedWith(annotation,true).isEmpty());

    DI.activatePackages(ServiceImplA.class.getPackage().getName());
    Assert.assertFalse(DI.getTypesAnnotatedWith(annotation).isEmpty());
    final Set<Class<?>> withInherit = DI.getTypesAnnotatedWith(annotation, false);
    Assert.assertFalse(withInherit.isEmpty());
    Assert.assertEquals(withInherit.size(), 2);

    final Set<Class<?>> withoutInherit = DI.getTypesAnnotatedWith(annotation, true);
    Assert.assertEquals(withoutInherit.size(), 1);
    Assert.assertFalse(withoutInherit.isEmpty());
  }
}
