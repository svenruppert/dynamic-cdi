package junit.org.rapidpm.ddi.reflectionmodel.v006;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import junit.org.rapidpm.ddi.reflectionmodel.v006.api.Service;
import junit.org.rapidpm.ddi.reflectionmodel.v006.model001.ServiceImpl001;
import junit.org.rapidpm.ddi.reflectionmodel.v006.model002.ServiceImpl002;
import junit.org.rapidpm.ddi.reflectionmodel.v006.model002.ServiceImpl002StaticMetricsProxy;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.ResponsibleFor;
import org.rapidpm.ddi.implresolver.ClassResolver;
import org.rapidpm.proxybuilder.core.metrics.RapidPMMetricsRegistry;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by svenruppert on 09.01.16.
 */
public class ReflectionModelTest006 {

  private final int endExclusive = 1_000;

  @Before
  public void setUp() throws Exception {
    DI.clearReflectionModel();
    DI.activatePackages(ReflectionModelTest006.class);
  }

  @Test
  public void test001() throws Exception {
    final Class<Service> serviceClass = Service.class;
    DI.activateMetrics(serviceClass);

    Assert.assertTrue(DI.isPkgPrefixActivated(ReflectionModelTest006.class));
    Assert.assertTrue(DI.isPkgPrefixActivated(ServiceImpl001.class));
    Assert.assertTrue(DI.isPkgPrefixActivated(ServiceImpl002.class));
    Assert.assertTrue(DI.isPkgPrefixActivated(Service.class));

    preCheckAndGetMetrics(serviceClass);

    final Service service = DI.activateDI(serviceClass);

    workOn(service);

    postCheck(serviceClass, endExclusive);
  }

  @NotNull
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
    final long count = metrics.histogram(histogrammName(serviceClass)).getCount();
    Assert.assertEquals(expectedCount, count);

  }

  private String histogrammName(Class clazz) {
    return clazz.getName() + ".doWork";
  }

  @Test
  public void test002() throws Exception {
    final Class<? extends Service> serviceClass = ServiceImpl001.class;

    DI.activateMetrics(Service.class);

    preCheckAndGetMetrics(serviceClass);
    workOn(DI.activateDI(serviceClass));
    postCheck(serviceClass, 0);

  }

  @Test
  public void test003() throws Exception {
    final Class<? extends Service> serviceClass = ServiceImpl002.class;
    DI.activateMetrics(Service.class); // will use ServiceImpl001

    preCheckAndGetMetrics(serviceClass);
    workOn(DI.activateDI(serviceClass));
    postCheck(serviceClass, 0);

    //check Imple

    DI.activateMetrics(serviceClass);
    preCheckAndGetMetrics(serviceClass);
    final Service serviceMetricsProxy = DI.activateDI(serviceClass);
    workOn(serviceMetricsProxy);
    postCheck(serviceClass, endExclusive);

    Assert.assertTrue(serviceMetricsProxy instanceof ServiceImpl002StaticMetricsProxy);

  }

  @ResponsibleFor(Service.class)
  public static class ServiceClassResolver implements ClassResolver<Service> {
    @Override
    public Class<? extends Service> resolve(final Class<Service> interf) {
      return ServiceImpl002.class;
    }
  }

}
