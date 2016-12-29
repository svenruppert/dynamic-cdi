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

package junit.org.rapidpm.ddi.classresolver.v002;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.ResponsibleFor;
import org.rapidpm.ddi.implresolver.ClassResolver;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

public class ClassResolverTest002 extends DDIBaseTest {

  @Test(expected = DDIModelException.class)
  public void testProducer001() throws Exception {
    final BusinessModule businessModule = new BusinessModule();

    try {
      DI.activateDI(businessModule);
    } catch (Exception e) {
      final Class<? extends Exception> aClass = e.getClass();
      Assert.assertEquals(DDIModelException.class, aClass);
      final String message = e.getMessage();
      Assert.assertTrue(message.startsWith("interface with multiple implementations"));
      Assert.assertTrue(message.contains("more as 1 ClassResolver"));
      Assert.assertTrue(message.contains("ClassResolverTest002$Service"));
      throw e;
    }
  }

  public interface Service {
    String work(String txt);

    SubService getSubService();

    boolean isPostconstructed();
  }

  @ResponsibleFor(Service.class)
  public static class ServiceClassResolverA implements ClassResolver<Service> {
    @Override
    public Class<? extends Service> resolve(final Class<Service> interf) {
      return ServiceImplA.class;
    }
  }

  @ResponsibleFor(Service.class)
  public static class ServiceClassResolverB implements ClassResolver<Service> {
    @Override
    public Class<? extends Service> resolve(final Class<Service> interf) {
      return ServiceImplB.class;
    }
  }

  public static class BusinessModule {
    @Inject Service service;

    public String work(String txt) {
      return service.work(txt);
    }
  }

  public static class ServiceImplA implements Service {
    @Inject SubService subService;
    boolean postconstructed;

    public String work(String txt) {
      return subService.work(txt);
    }

    @PostConstruct
    public void postconstruct() {
      postconstructed = true;
    }    @Override
    public SubService getSubService() {
      return subService;
    }



    public boolean isPostconstructed() {
      return postconstructed;
    }


  }

  public static class ServiceImplB implements Service {
    @Inject SubService subService;
    boolean postconstructed;

    @PostConstruct
    public void postconstruct() {
      postconstructed = true;
    }    public String work(String txt) {
      return subService.work(txt);
    }



    @Override
    public SubService getSubService() {
      return subService;
    }


    public boolean isPostconstructed() {
      return postconstructed;
    }


  }

  public static class SubService {
    @Inject SubSubService subSubService;
    boolean postconstructed;

    public String work(final String txt) {
      return subSubService.work(txt);
    }

    public boolean isPostconstructed() {
      return postconstructed;
    }

    @PostConstruct
    public void postconstruct() {
      postconstructed = true;
    }
  }

  public static class SubSubService {
    public String work(final String txt) {
      return "SubSubService " + txt;
    }
  }


}
