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

package junit.org.rapidpm.ddi;

import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Created by Sven Ruppert on 06.12.2014.
 */
public class DITest004 extends DDIBaseTest {

  @Test
  public void testInjection001() throws Exception {
    Service service = new Service();

    Assert.assertFalse(service.postconstructed);
    DI.activateDI(service);
    Assert.assertTrue(service.postconstructed);

    Assert.assertNotNull(service.subService);
    Assert.assertTrue(service.subService.postconstructed);

    Assert.assertNotNull(service.subService.subSubService);
    Assert.assertEquals("SubSubService test", service.work("test"));
  }

  public static class Service{
    @Inject SubService subService;
    public String work(String txt){
      return subService.work(txt);
    }
    boolean postconstructed = false;
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
