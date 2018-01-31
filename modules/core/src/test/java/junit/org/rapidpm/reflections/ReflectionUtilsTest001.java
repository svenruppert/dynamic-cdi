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

package junit.org.rapidpm.reflections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rapidpm.ddi.reflections.ReflectionUtils;


public class ReflectionUtilsTest001 {


  @Test
  public void test001() throws Exception {

    ReflectionUtils utils = new ReflectionUtils();

    Assertions.assertTrue(Service.class.isInterface());
    Assertions.assertTrue(ServiceA.class.isInterface());

    Assertions.assertNull(ServiceA.class.getSuperclass());

    Assertions.assertTrue(utils.checkInterface(Service.class, Service.class));
    Assertions.assertTrue(utils.checkInterface(ServiceA.class, Service.class));
    Assertions.assertTrue(utils.checkInterface(ServiceB.class, Service.class));
//
    Assertions.assertTrue(utils.checkInterface(ServiceImplA.class, Service.class));
    Assertions.assertTrue(utils.checkInterface(ServiceImplB.class, Service.class));
    Assertions.assertTrue(utils.checkInterface(ServiceImplAB.class, Service.class));
    Assertions.assertTrue(utils.checkInterface(ServiceImplBB.class, Service.class));

    Assertions.assertFalse(utils.checkInterface(A.class, Service.class));
    Assertions.assertTrue(utils.checkInterface(B.class, Service.class));
    Assertions.assertTrue(utils.checkInterface(C.class, Service.class));
    Assertions.assertTrue(utils.checkInterface(D.class, Service.class));
    Assertions.assertTrue(utils.checkInterface(E.class, Service.class));

    Assertions.assertTrue(utils.checkInterface(F.class, Service.class));
  }


  public interface Service {
  }

  public interface ServiceA extends Service {
  }

  public interface ServiceB extends ServiceA {
  }

  public static class ServiceImplA implements Service {
  }

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

  public static class C extends B {
  }

  public static class D extends A implements ServiceA {
  }

  public static class E extends A implements ServiceB {
  }

  public static class F extends ServiceImplAB {
  }


}
