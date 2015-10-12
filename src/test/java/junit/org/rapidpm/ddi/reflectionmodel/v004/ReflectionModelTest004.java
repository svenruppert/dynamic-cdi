package junit.org.rapidpm.ddi.reflectionmodel.v004;

import junit.org.rapidpm.ddi.reflectionmodel.v004.api.DemoAnnotation;
import junit.org.rapidpm.ddi.reflectionmodel.v004.api.Service;
import junit.org.rapidpm.ddi.reflectionmodel.v004.model001.ServiceImplA;
import junit.org.rapidpm.ddi.reflectionmodel.v004.model002.ServiceImplB;
import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;

import java.lang.annotation.Annotation;
import java.util.Set;


/**
 * Created by svenruppert on 12.10.15.
 */
public class ReflectionModelTest004 {

  private String checkPkgIsNotActivated() {
    final Package aPackage = Service.class.getPackage();
    final String aPackageName = aPackage.getName();
    Assert.assertFalse(DI.isPkgPrefixActivated(aPackageName));
    return aPackageName;
  }

  @Test
  public void test001_a() throws Exception {
    preCheck();

    DI.activatePackages(ServiceImplA.class.getPackage().getName());

    Assert.assertFalse(DI.getTypesAnnotatedWith(DemoAnnotation.class).isEmpty());
    Assert.assertFalse(DI.getTypesAnnotatedWith(DemoAnnotation.class,false).isEmpty());
    Assert.assertFalse(DI.getTypesAnnotatedWith(DemoAnnotation.class,true).isEmpty());
  }

  @Test
  public void test001_b() throws Exception {
    preCheck();

    DI.activatePackages(ServiceImplA.class.getPackage().getName());

    Annotation annotation = () -> DemoAnnotation.class;
    Assert.assertFalse(DI.getTypesAnnotatedWith(annotation).isEmpty());
    Assert.assertFalse(DI.getTypesAnnotatedWith(annotation,false).isEmpty());
    Assert.assertFalse(DI.getTypesAnnotatedWith(annotation,true).isEmpty());
  }

  private void preCheck() {
    DI.clearReflectionModel();

    DI.activatePackages("org.rapidpm");
    DI.activatePackages(checkPkgIsNotActivated());

    Assert.assertTrue(DI.getTypesAnnotatedWith(DemoAnnotation.class).isEmpty());
  }

  @Test
  public void test002_a() throws Exception {
    preCheck();

    DI.activatePackages(ServiceImplB.class.getPackage().getName());

    Assert.assertTrue(DI.getTypesAnnotatedWith(DemoAnnotation.class).isEmpty());
    Assert.assertTrue(DI.getTypesAnnotatedWith(DemoAnnotation.class,false).isEmpty());
    Assert.assertTrue(DI.getTypesAnnotatedWith(DemoAnnotation.class,true).isEmpty());

    DI.activatePackages(ServiceImplA.class.getPackage().getName());
    Assert.assertFalse(DI.getTypesAnnotatedWith(DemoAnnotation.class).isEmpty());
    final Set<Class<?>> withInherit = DI.getTypesAnnotatedWith(DemoAnnotation.class, false);
    Assert.assertFalse(withInherit.isEmpty());
    Assert.assertEquals(withInherit.size(), 2);

    final Set<Class<?>> withoutInherit = DI.getTypesAnnotatedWith(DemoAnnotation.class, true);
    Assert.assertEquals(withoutInherit.size(), 1);
    Assert.assertFalse(withoutInherit.isEmpty());
  }

  @Test
  public void test002_b() throws Exception {
    preCheck();

    DI.activatePackages(ServiceImplB.class.getPackage().getName());

    Annotation annotation = () -> DemoAnnotation.class;

    Assert.assertTrue(DI.getTypesAnnotatedWith(annotation).isEmpty());
    Assert.assertTrue(DI.getTypesAnnotatedWith(annotation,false).isEmpty());
    Assert.assertTrue(DI.getTypesAnnotatedWith(annotation,true).isEmpty());

    DI.activatePackages(ServiceImplA.class.getPackage().getName());
    Assert.assertFalse(DI.getTypesAnnotatedWith(annotation).isEmpty());
    final Set<Class<?>> withInherit = DI.getTypesAnnotatedWith(annotation, false);
    Assert.assertFalse(withInherit.isEmpty());
    Assert.assertEquals(withInherit.size(), 2);

    final Set<Class<?>> withoutInherit = DI.getTypesAnnotatedWith(annotation, true);
    Assert.assertEquals(withoutInherit.size(), 1);
    Assert.assertFalse(withoutInherit.isEmpty());
  }
}
