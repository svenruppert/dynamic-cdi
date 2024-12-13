/*
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
package junit.com.svenruppert.ddi.classresolver.v003;

import junit.com.svenruppert.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.svenruppert.ddi.DI;
import com.svenruppert.ddi.ResponsibleFor;
import com.svenruppert.ddi.implresolver.ClassResolver;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

public class ClassResolver003Test
    extends DDIBaseTest {


  @Test
  public void testProducer001() {

    final BusinessModule businessModule = new BusinessModule();
    DI.activateDI(businessModule);
    Assertions.assertNotNull(businessModule);
    Assertions.assertNotNull(businessModule.service);
    Assertions.assertEquals(ServiceImplB.class, businessModule.service.getClass());
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
    @Inject ServiceImplB service; //Here the concrete Class

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
    }

    @Override
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
    }

    public String work(String txt) {
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
