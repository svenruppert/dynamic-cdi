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

package junit.org.rapidpm.ddi.named;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.DI;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Multiple implementations of an Interface, no decission possible
 *
 * Created by RapidPM - Team on 07.12.2014.
 */
public class NamedTest002 extends DDIBaseTest {


  @Test()
  public void testInjection001() throws Exception {
    BusinessModule businessModule = new BusinessModule();
    Assertions.assertThrows(DDIModelException.class, ()-> DI.activateDI(businessModule));
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


    @Override
    public String work(final String txt) {
      return null;
    }

    @PostConstruct
    public void postconstruct() {
    }

    @Override
    public SubService getSubService() {
      return null;
    }


    public boolean isPostconstructed() {
      return false;
    }


  }

  public static class ServiceImplB implements Service {

    boolean postconstructed;

    @PostConstruct
    public void postconstruct() {
      postconstructed = true;
    }    public String work(String txt) {
      return null;
    }



    @Override
    public SubService getSubService() {
      return null;
    }


    public boolean isPostconstructed() {
      return postconstructed;
    }


  }

  public static class SubService {
  }


}
