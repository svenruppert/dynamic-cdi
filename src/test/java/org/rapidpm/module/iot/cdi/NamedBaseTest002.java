/*
 * Copyright [2014] [www.rapidpm.org / Sven Ruppert (sven.ruppert@rapidpm.org)]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.rapidpm.module.iot.cdi;

import org.junit.Assert;
import org.junit.Test;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Created by Sven Ruppert on 07.12.2014.
 */
public class NamedBaseTest002 {
  @Test
  public void testInjection001() throws Exception {
    BusinessModule businessModule = new BusinessModule();

    final DI di = new DI();


    di.activateCDI(businessModule);
    Assert.assertNotNull(businessModule.service);

    //Exception da Multiplizitaeten

    Assert.assertTrue(businessModule.service.isPostconstructed());

    Assert.assertNotNull(businessModule.service.getSubService());
    Assert.assertTrue(businessModule.service.getSubService().postconstructed);

    Assert.assertEquals("SubSubService test", businessModule.work("test"));
  }

  public static class BusinessModule{

    @Inject Service service;

    public String work(String txt){
      return service.work(txt);
    }
  }


  public static interface Service{
    public String work(String txt);
    public SubService getSubService();
    public boolean isPostconstructed();
  }

  public static class ServiceImplA implements Service {
    @Inject SubService subService;
    public String work(String txt){
      return subService.work(txt);
    }

    @Override
    public SubService getSubService() {
      return subService;
    }

    boolean postconstructed = false;

    public boolean isPostconstructed() {
      return postconstructed;
    }

    @PostConstruct
    public void postconstruct(){
      postconstructed = true;
    }
  }
  public static class ServiceImplB implements Service {
    @Inject SubService subService;
    public String work(String txt){
      return subService.work(txt);
    }

    @Override
    public SubService getSubService() {
      return subService;
    }

    boolean postconstructed = false;
    public boolean isPostconstructed() {
      return postconstructed;
    }

    @PostConstruct
    public void postconstruct(){
      postconstructed = true;
    }
  }

  public static class SubService{
    @Inject SubSubService subSubService;
    public String work(final String txt){
      return subSubService.work(txt);
    }
    boolean postconstructed = false;
    public boolean isPostconstructed() {
      return postconstructed;
    }
    @PostConstruct
    public void postconstruct(){
      postconstructed = true;
    }
  }

  public static class SubSubService{
    public String work(final String txt){
      return "SubSubService " + txt;
    }
  }
}
