package junit.org.rapidpm.ddi.proxy.v004;

import org.rapidpm.ddi.producer.Producer;

import org.rapidpm.ddi.Produces;

/**
 * Created by svenruppert on 17.08.15.
 */
@Produces(Service.class)
public class ServiceProducer implements Producer<Service> {

  public Service create() {
    return ServiceAdapterBuilder.newBuilder()
        .setOriginal(null)
        .withDoWork(txt -> "mocked-" + txt)
        .buildForTarget(Service.class);
  }
}
