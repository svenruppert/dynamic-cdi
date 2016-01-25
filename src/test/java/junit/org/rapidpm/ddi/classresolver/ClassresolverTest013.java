package junit.org.rapidpm.ddi.classresolver;

import org.junit.Test;
import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.ResponsibleFor;
import org.rapidpm.ddi.implresolver.ClassResolver;

import javax.inject.Inject;

/**
 * Created by Sven Ruppert on 25.01.16.
 */
public class ClassresolverTest013 {

  @Test(expected = DDIModelException.class)
  public void test001() throws Exception {
    Service service = new Service();
    DI.activateDI(service);
  }

  public interface SubService {
  }

  public static class Service {
    @Inject SubService subService;
  }

  @ResponsibleFor(SubService.class)
  public static class SubServiceResolver implements ClassResolver<SubService> {

    public SubServiceResolver(String txt) {
    }

    @Override
    public Class<? extends SubService> resolve(Class<SubService> interf) {
      return SubServiceImplA.class;
    }
  }

  public static class SubServiceImplA implements SubService {
    public SubServiceImplA() {
      throw new RuntimeException("should never be be..");
    }
  }

  public static class SubServiceImplB implements SubService {
    public SubServiceImplB() {
      throw new RuntimeException("should never be be..");
    }
  }

}
