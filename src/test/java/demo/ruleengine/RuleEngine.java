package demo.ruleengine;

import demo.Main;

import javax.enterprise.util.AnnotationLiteral;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by sven on 20.04.15.
 */
public class RuleEngine {

  public  <T extends Annotation, C> AnnotationLiteral<T> resolve(Class<C> aClass){

    //pruefe ob aClass ein interface ist - vorerst
    //hole alle Implementierungen - moegliche Treffermenge
    //hole Alle RuleEngineSteps responsible for this class
    //check if only one will give true
    //use this one to resolve
    final Set<Class<?>> typesAnnotatedWith = Main.Classmodell.reflections.getTypesAnnotatedWith(Responsible.class);

    typesAnnotatedWith.forEach(System.out::println);

    final Set<Method> methodsReturn = Main.Classmodell.reflections.getMethodsReturn(Main.Service.class);
    for (Method method : methodsReturn) {
      System.out.println("method = " + method);
    }




    return null;


  }


  @FunctionalInterface
  public interface RuleStepInterface {
    <T extends Annotation> AnnotationLiteral<T> resolve();
  }

  @Responsible(tragetInterface = Main.Service.class)
  public class Step_A implements RuleStepInterface {
    public <T extends Annotation> AnnotationLiteral<T> resolve() {
      return null;
    }
  }
  @Responsible(tragetInterface = Main.Service.class)
  public class Step_B implements RuleStepInterface {
    public <T extends Annotation> AnnotationLiteral<T> resolve() {
      return null;
    }
  }




}
