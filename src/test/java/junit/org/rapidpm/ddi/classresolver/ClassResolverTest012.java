package junit.org.rapidpm.ddi.classresolver;

import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.ResponsibleFor;
import org.rapidpm.ddi.implresolver.ClassResolver;

import javax.inject.Inject;

/**
 * Created by b.bosch on 02.12.2015.
 */
public class ClassResolverTest012 {


  @Test(expected = RuntimeException.class)
  public void test001() throws Exception {
    Service service = new Service();
      DI.activateDI(service);
  }

  public static class Service{
    @Inject SubService subService;
  }

  public interface SubService{}

  @ResponsibleFor(SubService.class)
  public static class SubServiceResolver implements ClassResolver<SubService> {

    @Override
    public Class<? extends SubService> resolve(Class<SubService> interf) {
      return FaultySubService.class;
    }
  }

  public static class FaultySubService implements SubService{
    public FaultySubService() {
      throw new RuntimeException("OOPS");
    }
  }
}