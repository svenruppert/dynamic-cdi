package junit.org.rapidpm.reflections.v002;

import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.reflections.ReflectionUtils;
import org.rapidpm.proxybuilder.objectadapter.annotations.staticobjectadapter.IsStaticObjectAdapter;
import org.rapidpm.proxybuilder.staticgenerated.annotations.IsGeneratedProxy;
import org.rapidpm.proxybuilder.staticgenerated.annotations.IsLoggingProxy;
import org.rapidpm.proxybuilder.staticgenerated.annotations.IsMetricsProxy;

import java.util.HashSet;
import java.util.Set;

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
public class ReflectionUtilsTest002 {


  @Test
  public void test001() throws Exception {
    final ReflectionUtils utils = new ReflectionUtils();

    final Set<Class<? extends Service>> subTypesOfService = new HashSet<>();
    subTypesOfService.add(Service.class);
    subTypesOfService.add(ServiceA.class);
    subTypesOfService.add(ServiceB.class);
    subTypesOfService.add(ServiceImplA.class);
    subTypesOfService.add(ServiceImplB.class);
    subTypesOfService.add(ServiceImplAB.class);
    subTypesOfService.add(ServiceImplBB.class);


    utils.removeInterfacesAndGeneratedFromSubTypes(subTypesOfService);

    Assert.assertFalse(subTypesOfService.contains(Service.class));
    Assert.assertFalse(subTypesOfService.contains(ServiceA.class));
    Assert.assertFalse(subTypesOfService.contains(ServiceB.class));
    Assert.assertFalse(subTypesOfService.contains(ServiceImplA.class));
    Assert.assertFalse(subTypesOfService.contains(ServiceImplB.class));
    Assert.assertTrue(subTypesOfService.contains(ServiceImplAB.class));
    Assert.assertTrue(subTypesOfService.contains(ServiceImplBB.class));

    final Set<Class<? extends A>> subTypesOfA = new HashSet<>();
    subTypesOfA.add(A.class);
    subTypesOfA.add(B.class);
    subTypesOfA.add(C.class);
    subTypesOfA.add(D.class);
    subTypesOfA.add(E.class);
    utils.removeInterfacesAndGeneratedFromSubTypes(subTypesOfA);

    Assert.assertTrue(subTypesOfA.contains(A.class));
    Assert.assertTrue(subTypesOfA.contains(B.class));
    Assert.assertFalse(subTypesOfA.contains(C.class));
    Assert.assertFalse(subTypesOfA.contains(D.class));
    Assert.assertTrue(subTypesOfA.contains(E.class));

  }

  public interface Service {
  }

  public interface ServiceA extends Service {
  }

  public interface ServiceB extends ServiceA {
  }

  @IsGeneratedProxy
  public static class ServiceImplA implements Service {
  }

  @IsMetricsProxy
  public static class ServiceImplB implements ServiceA {
  }

  public static class ServiceImplAB extends ServiceImplA {
  }

  public static class ServiceImplBB extends ServiceImplB {
  }

  public static class A {
  }

  public static class B extends A implements Service {
  }

  @IsStaticObjectAdapter
  public static class C extends B {
  }

  @IsLoggingProxy
  public static class D extends A implements ServiceA {
  }

  public static class E extends A implements ServiceB {
  }

  public static class F extends ServiceImplAB {
  }


}
