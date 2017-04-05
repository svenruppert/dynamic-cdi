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

package junit.org.rapidpm.ddi.reflectionmodel.v005;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import junit.org.rapidpm.ddi.reflectionmodel.v005.api.Service;
import junit.org.rapidpm.ddi.reflectionmodel.v005.model001.ServiceImpl001;
import junit.org.rapidpm.ddi.reflectionmodel.v005.model002.ServiceImpl002;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.ResponsibleFor;
import org.rapidpm.ddi.implresolver.ClassResolver;
import org.rapidpm.proxybuilder.core.metrics.RapidPMMetricsRegistry;

import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ReflectionModelTest005 {

  private final int endExclusive = 1_000;

  @Before
  public void setUp() throws Exception {
    DI.clearReflectionModel();
  }

  @Test
  public void test001() throws Exception {
    DI.activatePackages(ReflectionModelTest005.class);
    final Class<Service> serviceClass = Service.class;
    DI.activateMetrics(serviceClass);

    Assert.assertTrue(DI.isPkgPrefixActivated(ReflectionModelTest005.class));
    Assert.assertTrue(DI.isPkgPrefixActivated(ServiceImpl001.class));
    Assert.assertTrue(DI.isPkgPrefixActivated(ServiceImpl002.class));
    Assert.assertTrue(DI.isPkgPrefixActivated(serviceClass));

    preCheckAndGetMetrics(serviceClass);

    final Service service = DI.activateDI(serviceClass);

    workOn(service);

    postCheck(serviceClass, endExclusive);
  }

  private MetricRegistry preCheckAndGetMetrics(final Class<? extends Service> serviceClass) {
    final RapidPMMetricsRegistry metricsRegistry = RapidPMMetricsRegistry.getInstance();
    final MetricRegistry metrics = metricsRegistry.getMetrics();
    final Histogram histogram = metrics.histogram(histogrammName(serviceClass));
    Assert.assertNotNull(histogram);
    final long count = metrics.histogram(histogrammName(serviceClass)).getCount();
    Assert.assertEquals(0, count);
    return metrics;
  }

  private void workOn(final Service service) {

    final Set<String> collect = IntStream.range(0, endExclusive)
        .boxed()
        .map(v -> service.doWork(v + ""))
        .collect(Collectors.toSet());
    Assert.assertEquals(endExclusive, collect.size());
  }

  private void postCheck(final Class<? extends Service> serviceClass, int expectedCount) {
    final RapidPMMetricsRegistry metricsRegistry = RapidPMMetricsRegistry.getInstance();
    final MetricRegistry metrics = metricsRegistry.getMetrics();
    final SortedMap<String, Histogram> histograms = metrics.getHistograms();

    for (Entry<String, Histogram> entry : histograms.entrySet()) {
      System.out.println("key = " + entry.getKey());
      final long count = entry.getValue().getCount();
      System.out.println("count = " + count);

    }
    final long count = metrics.histogram(histogrammName(serviceClass)).getCount();
    Assert.assertEquals(expectedCount, count);

  }

  private String histogrammName(Class clazz) {
    final String histogrammName = clazz.getName() + ".doWork";
    System.out.println("histogrammName = " + histogrammName);
    return histogrammName;
  }

  @Test
  public void test002() throws Exception {
    DI.activatePackages(ReflectionModelTest005.class);
    final Class<? extends Service> serviceClass = ServiceImpl001.class;

    DI.activateMetrics(Service.class);

    Assert.assertTrue(DI.isPkgPrefixActivated(ReflectionModelTest005.class));
    Assert.assertTrue(DI.isPkgPrefixActivated(ServiceImpl001.class));
    Assert.assertTrue(DI.isPkgPrefixActivated(ServiceImpl002.class));
    Assert.assertTrue(DI.isPkgPrefixActivated(Service.class));

    final MetricRegistry metrics = preCheckAndGetMetrics(serviceClass);

    final Service service = DI.activateDI(serviceClass);
    workOn(service);

    postCheck(serviceClass, 0);
  }

  @Test
  public void test003() throws Exception {
    DI.activatePackages(ReflectionModelTest005.class);
    final Class<? extends Service> serviceClass = ServiceImpl002.class;

    DI.activateMetrics(Service.class); // will use ServiceImpl001

    Assert.assertTrue(DI.isPkgPrefixActivated(ReflectionModelTest005.class));
    Assert.assertTrue(DI.isPkgPrefixActivated(ServiceImpl001.class));
    Assert.assertTrue(DI.isPkgPrefixActivated(ServiceImpl002.class));
    Assert.assertTrue(DI.isPkgPrefixActivated(Service.class));

    final MetricRegistry metrics = preCheckAndGetMetrics(serviceClass);

    final Service service = DI.activateDI(serviceClass);
    workOn(service);

    postCheck(serviceClass, 0);

  }

  @ResponsibleFor(Service.class)
  public static class ServiceClassResolver implements ClassResolver<Service> {
    @Override
    public Class<? extends Service> resolve(final Class<Service> interf) {
      return ServiceImpl001.class;
    }
  }


}
