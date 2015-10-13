package junit.org.rapidpm.ddi.classresolver;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;

import javax.inject.Inject;
import java.util.Set;

/**
 * Created by svenruppert on 13.10.15.
 */
public class ClassResolverTest010 extends DDIBaseTest {

@Inject Service service;

  @Test
  public void test001() throws Exception {

    final Set<Class<? extends Service>> subTypesOf = DI.getSubTypesOf(Service.class);
    for (Class<? extends Service> aClass : subTypesOf) {
      System.out.println("aClass = " + aClass);
      System.out.println("aClass.isInterface() = " + aClass.isInterface());


    }



    DI.activateDI(this);
    Assert.assertNotNull(service);

  }

  public interface Service {
    String doWork(String str);
  }
  public interface ServiceA extends Service {
    String doWork(String str);
  }
  public interface ServiceB extends Service  {
    String doWork(String str);
  }
  public interface ServiceAA extends ServiceA  {
    String doWork(String str);
  }

  public static class ServiceImpl implements ServiceAA{

    @Override
    public String doWork(final String str) {
      return "ServiceImpl " + str;
    }
  }


}
