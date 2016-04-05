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

package junit.org.rapidpm.ddi;

import org.junit.gen5.api.AfterEach;
import org.junit.gen5.api.BeforeEach;
import org.rapidpm.ddi.DI;

public class DDIBaseTest {


  @BeforeEach
  public void initDDI() throws Exception {
    DI.clearReflectionModel();
    DI.activatePackages("org.rapidpm");
    final String name = this.getClass().getPackage().getName();
    System.out.println("name = " + name);
    DI.activatePackages(name);
  }

  @AfterEach
  public void tearDownDDI() throws Exception {
    DI.clearReflectionModel();
  }
}
