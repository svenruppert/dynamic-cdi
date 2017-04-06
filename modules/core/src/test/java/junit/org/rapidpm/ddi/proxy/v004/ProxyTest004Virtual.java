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

package junit.org.rapidpm.ddi.proxy.v004;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.Proxy;
import org.rapidpm.proxybuilder.type.dymamic.virtual.VirtualDynamicProxyInvocationHandler;
import org.rapidpm.proxybuilder.type.dymamic.virtual.creationstrategy.ServiceStrategyFactoryNotThreadSafe;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.util.Optional;

public class ProxyTest004Virtual extends DDIBaseTest {

//  @Test(expected = DDIModelException.class)
//  public void test001() throws Exception {
//    try {
//      final BusinessModule001 businessModule001 = DI.activateDI(new BusinessModule001());
//    } catch (Exception e) {
//      if (e instanceof DDIModelException) {
//        final String message = e.getMessage();
//        System.out.println("message = " + message);
//        Assert.assertTrue(message.contains("only interfaces found for interface"));
//        throw e;
//      } else {
//        Assert.fail();
//      }
//    }
//  }

  @Test
  public void test002() throws Exception {
    DI.activatePackages("junit.org.rapidpm"); //expliziet activierte pkgs
    final BusinessModule001 businessModule001 = DI.activateDI(new BusinessModule001());
    Assert.assertNotNull(businessModule001);
    Assert.assertNotNull(businessModule001.service);
    Assert.assertTrue(businessModule001.post);
  }

  @Test
  public void test003() throws Exception {
    DI.activatePackages("org.rapidpm");
    DI.activatePackages("junit.org.rapidpm");
    final BusinessModule002 businessModule = DI.activateDI(new BusinessModule002());

    Assert.assertNotNull(businessModule);
    Assert.assertTrue(businessModule.post);

    final Service service = businessModule.service;
    Assert.assertNotNull(service);
    Assert.assertTrue(java.lang.reflect.Proxy.isProxyClass(service.getClass()));
  }

  @Test
  public void test004() throws Exception {
    DI.activatePackages("org.rapidpm");
    DI.activatePackages("junit.org.rapidpm");
    final BusinessModule002 businessModule = DI.activateDI(new BusinessModule002());
    Assert.assertNotNull(businessModule);
    Assert.assertTrue(businessModule.post);
    final Service service = businessModule.service;
    Assert.assertNotNull(service);

    final Optional<Service> optional = checkinternalrealService(service);
    Assert.assertFalse(optional.isPresent());
    final String hoppel = service.doWork("Hoppel");
    Assert.assertNotNull(hoppel);
    Assert.assertTrue(checkinternalrealService(service).isPresent());
    Assert.assertEquals("mocked-Hoppel", hoppel);

  }

  private Optional<Service> checkinternalrealService(final Service service) throws NoSuchFieldException, IllegalAccessException {
    Assert.assertTrue(java.lang.reflect.Proxy.isProxyClass(service.getClass()));

    final InvocationHandler invocationHandler = java.lang.reflect.Proxy.getInvocationHandler(service);
    Assert.assertNotNull(invocationHandler);
    Assert.assertTrue(invocationHandler instanceof VirtualDynamicProxyInvocationHandler);

    final Field serviceStrategyFactory = invocationHandler.getClass().getDeclaredField("serviceStrategyFactory");
    Assert.assertNotNull(serviceStrategyFactory);
    final boolean accessible = serviceStrategyFactory.isAccessible();
    serviceStrategyFactory.setAccessible(true);
    final ServiceStrategyFactoryNotThreadSafe realServiceStrategieFactory = (ServiceStrategyFactoryNotThreadSafe) serviceStrategyFactory.get(invocationHandler);
    serviceStrategyFactory.setAccessible(accessible);

    final Field realServiceField = ServiceStrategyFactoryNotThreadSafe.class.getDeclaredField("service");
    Assert.assertNotNull(realServiceField);
    final boolean accessible1 = realServiceField.isAccessible();
    realServiceField.setAccessible(true);
    final Optional result = Optional.ofNullable(realServiceField.get(realServiceStrategieFactory));
    realServiceField.setAccessible(accessible1);
    return result;
  }

  public static class BusinessModule001 {
    @Inject Service service;

    boolean post;

    @PostConstruct
    public void postconstruct() {
      post = true;
    }
  }

  public static class BusinessModule002 {
    @Inject @Proxy(virtual = true) Service service;

    boolean post;

    @PostConstruct
    public void postconstruct() {
      post = true;
    }
  }


}
