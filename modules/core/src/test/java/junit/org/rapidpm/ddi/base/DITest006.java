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
package junit.org.rapidpm.ddi.base;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rapidpm.ddi.DI;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

public class DITest006 extends DDIBaseTest {
  static boolean postconstructA1;
  static boolean postconstructA2;
  static boolean postconstructB1;
  static boolean postconstructB2;

//  @BeforeEach
//  public void setUp() throws Exception {
//    DI.clearReflectionModel();
//    DI.activatePackages(DITest006.class.getPackage().getName());
//  }

  @Test
  public void test001() throws Exception {
    Assertions.assertFalse(postconstructA1);
    Assertions.assertFalse(postconstructA2);
    Assertions.assertFalse(postconstructB1);
    Assertions.assertFalse(postconstructB2);

    final BusinessModule businessModule = new BusinessModule();
    DI.activateDI(businessModule);

    Assertions.assertTrue(postconstructA1);
    Assertions.assertTrue(postconstructA2);
    Assertions.assertTrue(postconstructB1);
    Assertions.assertTrue(postconstructB2);


  }

  public interface Service {
    String doWork(String txt);
  }

  public static class ServiceImpl implements Service {
    @Override
    public String doWork(final String txt) {
      return this.getClass().getSimpleName() + " " + txt;
    }

    @PostConstruct
    public void post001() {
      if (postconstructA1 == true) Assertions.fail("too bad..");
      postconstructA1 = true;
    }

    @PostConstruct
    public void post002() {
      if (postconstructA2 == true) Assertions.fail("too bad..");
      postconstructA2 = true;
    }
  }

  public static class BusinessModule {
    @Inject Service service;

    @PostConstruct
    public void post001() {
      if (postconstructB1 == true) Assertions.fail("too bad..");
      postconstructB1 = true;
    }

    @PostConstruct
    public void post002() {
      if (postconstructB2 == true) Assertions.fail("too bad..");
      postconstructB2 = true;
    }

  }


}

