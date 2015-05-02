package demo;

import demo.ruleengine.RuleEngine;
import org.reflections.Reflections;
import org.reflections.scanners.*;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.Collections;
import java.util.Set;

/**
 * Created by sven on 20.04.15.
 */
public class Main {

  public static class Classmodell {

    public static final FilterBuilder TestModelFilter = new FilterBuilder().include("demo.*");

    public static final Reflections reflections = new Reflections(new ConfigurationBuilder()
        .setUrls(Collections.singletonList(ClasspathHelper.forClass(Main.class)))
        .filterInputsBy(TestModelFilter)
        .setScanners(
            new SubTypesScanner(false),
            new TypeAnnotationsScanner(),
            new FieldAnnotationsScanner(),
            new MethodAnnotationsScanner(),
            new MethodParameterScanner(),
            new MethodParameterNamesScanner(),
            new MemberUsageScanner()));


  }

  public static void main(String[] args) {

    final Set<String> allTypes = Classmodell.reflections.getAllTypes();
    allTypes.forEach(System.out::println);

    final Set<Class<? extends Service>> subTypesOf = Classmodell.reflections.getSubTypesOf(Service.class);
    subTypesOf.forEach(System.out::println);

    new RuleEngine().resolve(null);


  }


  @Inject Service service;







  public interface Service {
    String work();
  }

  public static class ServiceImpl_A implements Service {
    public String work() {
      return "Impl_A";
    }
  }

  public static class ServiceImpl_B implements Service {
    public String work() {
      return "Impl_B";
    }
  }


  public static class Context {
    public Boolean orig = false;
  }

//  public static interface Producer {
//
//  }

  public static class ProducerImpl {

    @Inject Context ctx;

    public <T> T create(Class<T> targetInterface){
      //treffe entscheidung
      return null;
    }
  }

  public static class Producer_A {
    @Produces @Impl_A
    public Service create_A() {
      return new ServiceImpl_A();
    }
  }

  public static class Producer_B {
    @Produces @Impl_B
    public Service create_B() {
      return new ServiceImpl_B();
    }
  }


}
