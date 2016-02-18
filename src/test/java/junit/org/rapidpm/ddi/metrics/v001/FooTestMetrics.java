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

package junit.org.rapidpm.ddi.metrics.v001;

import junit.org.rapidpm.ddi.metrics.v001.foo.FooLeft;
import junit.org.rapidpm.ddi.metrics.v001.foo.FooRight;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.proxybuilder.core.metrics.RapidPMMetricsRegistry;

/**
 * Created by b.bosch on 17.02.2016.
 */
public class FooTestMetrics {
  @Before
  public void init() {
    DI.clearReflectionModel();
    DI.activatePackages(this.getClass());
    DI.activateMetrics(FooLeft.class);
    DI.activateMetrics(FooRight.class);
  }

  @Test
  @Ignore
  public void test001() throws Exception {

    final FooRight fooRight = DI.activateDI(FooRight.class);
    final FooLeft fooLeft = DI.activateDI(FooLeft.class);

    for (int i = 0; i < 10; i++) {
      fooLeft.getLeft();
      fooRight.getRight();
    }
    RapidPMMetricsRegistry.getInstance().getMetrics().getMeters();


  }
}

