package junit.org.rapidpm.ddi.classresolver.v016;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rapidpm.ddi.DI;

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
 * Created by RapidPM - Team on 02.06.16.
 */
public class ClassresolverTest016 {


  @Before
  public void setUp() throws Exception {
    DI.clearReflectionModel();
    DI.activatePackages("org.rapidpm");
    DI.activatePackages(this.getClass());
  }

  @After
  public void tearDown() throws Exception {
    DI.clearReflectionModel();
  }

  @Test
  public void test001() throws Exception {
    Assert.assertEquals(ServiceA.class, DI.activateDI(Service.class).getClass());
  }

}
