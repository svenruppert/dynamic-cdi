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
package junit.com.svenruppert.ddi.named;

import junit.com.svenruppert.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.svenruppert.ddi.DI;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

public class Named003Test
    extends DDIBaseTest {


  @Test
  public void testInjection001() {
    BusinessModule businessModule = new BusinessModule();

    DI.activateDI(businessModule);
    Assertions.assertNotNull(businessModule.service);
    Assertions.assertTrue(businessModule.service.isPostconstructed());

    Assertions.assertNotNull(businessModule.service.getSubService());
    Assertions.assertTrue(businessModule.service.getSubService().postconstructed);

    Assertions.assertEquals("SubSubService test", businessModule.work("test"));
  }


  public interface Service {
    String work(String txt);

    SubService getSubService();

    boolean isPostconstructed();
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

    @Override
    public SubService getSubService() {
      return subService;
    }

    public boolean isPostconstructed() {
      return postconstructed;
    }

    @PostConstruct
    public void postconstruct() {
      postconstructed = true;
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
