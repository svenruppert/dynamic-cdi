package junit.org.rapidpm.ddi.proxy.v004;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.Proxy;
import org.rapidpm.proxybuilder.core.metrics.RapidPMMetricsRegistry;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Created by svenruppert on 17.08.15.
 */
public class ProxyTest004Metrics extends DDIBaseTest {


  String s1;
  private ConsoleReporter reporter;

  @Before
  public void setUp() throws Exception {
    final MetricRegistry metrics = RapidPMMetricsRegistry.getInstance().getMetrics();
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

  @Test
  public void test004() throws Exception {
    DI.activatePackages("org.rapidpm");
    DI.activatePackages("junit.org.rapidpm");
    final BusinessModule003 businessModule001 = DI.activateDI(new BusinessModule003());
    Assert.assertNotNull(businessModule001);
    final Service service = businessModule001.service;
    Assert.assertNotNull(service);
    Assert.assertTrue(businessModule001.post);

    IntStream.range(0, 10_000_000).forEach(i -> {
      final String s = service.doWork(i + "");
      workingHole(s.toUpperCase());
    });

    final SortedMap<String, Histogram> histograms = RapidPMMetricsRegistry.getInstance().getMetrics().getHistograms();
    Assert.assertNotNull(histograms);
    Assert.assertFalse(histograms.isEmpty());

    final Histogram histogram = histograms.get(Service.class.getName() + ".doWork");
    final long count = histogram.getCount();
    Assert.assertTrue(count >= 1);


    System.out.println("s1 = " + s1);


  }

  private void workingHole(String s) {
    s1 = s;
  }

  public static class BusinessModule003 {
    @Inject @Proxy(metrics = true) Service service;

    boolean post = false;

    @PostConstruct
    public void postconstruct() {
      post = true;
    }
  }


}
