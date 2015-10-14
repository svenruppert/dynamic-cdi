package junit.javax.enterprise.util;

import org.junit.Assert;
import org.junit.Test;

import javax.enterprise.util.AnnotationLiteral;

import java.lang.annotation.Annotation;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Created by svenruppert on 12.10.15.
 */
public class AnnotationLiteralTest {

  @Test
  public void testAnnotationType() throws Exception {




  }

  @Test
  public void testHashCode() throws Exception {

  }




  @Test
  public void testEquals01() throws Exception {

    AnnotationLiteral<TestAnnotation> l1 = new AnnotationLiteral<TestAnnotation>() {
      @Override
      public Class<? extends Annotation> annotationType() {
        return TestAnnotation.class;
      }
    };
    AnnotationLiteral<TestAnnotation> l2 = new AnnotationLiteral<TestAnnotation>() {
      @Override
      public Class<? extends Annotation> annotationType() {
        return TestAnnotation.class;
      }
    };
    Assert.assertTrue(l1.equals(l2));
    Assert.assertEquals(l1,l2);

    Assert.assertEquals(l1.annotationType(), TestAnnotation.class);
    Assert.assertEquals(l2.annotationType(), TestAnnotation.class);

  }
  @Test
  public void testEquals02() throws Exception {

    AnnotationLiteral<TestAnnotation> l1 = new AnnotationLiteral<TestAnnotation>() {
      @Override
      public Class<? extends Annotation> annotationType() {
        return TestAnnotation.class;
      }
    };
    AnnotationLiteral<WrongAnnotation> l2 = new AnnotationLiteral<WrongAnnotation>() {
      @Override
      public Class<? extends Annotation> annotationType() {
        return WrongAnnotation.class;
      }
    };

    Assert.assertFalse(l1.equals(l2));
    Assert.assertNotEquals(l1,l2);
    Assert.assertEquals(l1.annotationType(), TestAnnotation.class);
    Assert.assertEquals(l2.annotationType(), WrongAnnotation.class);
  }

  @Test
  public void testToString() throws Exception {
    AnnotationLiteralTest a = new AnnotationLiteralTest();
    System.out.println("a = " +a.toString());


    System.out.println("a.hashCode() = " + a.hashCode());

  }
}