package org.rapidpm.ddi.proxy;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.Proxy;
import org.rapidpm.proxybuilder.type.metrics.MetricsRegistry;

import javax.inject.Inject;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Created by svenruppert on 23.07.15.
 */
public class ProxyTest003 {

  private ConsoleReporter reporter;

  @Before
  public void setUp() throws Exception {
    final MetricRegistry metrics = MetricsRegistry.getInstance().getMetrics();
     reporter = ConsoleReporter.forRegistry(metrics)
        .convertRatesTo(TimeUnit.NANOSECONDS)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .build();
    reporter.start(1, TimeUnit.SECONDS);
  }

  @After
  public void tearDown() throws Exception {
    reporter.close();

  }

  String s1;
  private void workingHole(String s) {
    s1 = s;
  }


  // tests


  @Test
  public void test001() throws Exception {
    final BusinessModulMetrics demoLogic = new BusinessModulMetrics();
    DI.getInstance().activateDI(demoLogic);

    IntStream.range(0, 10_000_000).forEach(i -> {
      final String s = demoLogic.service.doWork(i+"");
      workingHole(s.toUpperCase());
    });

    final SortedMap<String, Histogram> histograms = MetricsRegistry.getInstance().getMetrics().getHistograms();
    Assert.assertNotNull(histograms);
    Assert.assertFalse(histograms.isEmpty());
    Assert.assertTrue(histograms.containsKey(Service.class.getSimpleName()));

    final Histogram histogram = histograms.get(Service.class.getSimpleName());
    Assert.assertNotNull(histogram);
    Assert.assertNotNull(histogram.getSnapshot());
    MetricsRegistry.getInstance().getMetrics().remove(Service.class.getSimpleName());
    System.out.println("s1 = " + s1);

  }

  @Test
  public void test002() throws Exception {
    final BusinessModul demoLogic = new BusinessModul();
    DI.getInstance().activateDI(demoLogic);

    IntStream.range(0, 10_000_000).forEach(i -> {
      final String s = demoLogic.service.doWork(i+"");
      workingHole(s.toUpperCase());
    });

    final SortedMap<String, Histogram> histograms = MetricsRegistry.getInstance().getMetrics().getHistograms();
    Assert.assertNotNull(histograms);
    Assert.assertTrue(histograms.isEmpty());

    System.out.println("s1 = " + s1);

  }

  public static class BusinessModulMetrics{
    @Inject @Proxy(metrics = true) Service service;
    public String work(String str){
      return service.doWork(str);
    }
  }

  public static class BusinessModul{
    @Inject @Proxy(metrics = false) Service service;
    public String work(String str){
      return service.doWork(str);
    }
  }

  public interface Service {
    String doWork(String str);
  }

  public static class ServiceA implements Service {

    public ServiceA() {
      System.out.println(" ServiceA = constructed...");
    }

    @Override
    public String doWork(final String str) {
      return "ServiceA_" + str;
    }
  }
}
