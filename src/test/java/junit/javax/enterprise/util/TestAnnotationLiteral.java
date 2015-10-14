package junit.javax.enterprise.util;

import javax.enterprise.util.AnnotationLiteral;
import java.lang.annotation.Annotation;

/**
 * Created by svenruppert on 13.10.15.
 */
public class TestAnnotationLiteral extends AnnotationLiteral<TestAnnotation> {

  public int i() {return 1;}
  public long l() {return 1;}
  public byte b()  {return 1;}
  public short s()  {return 1;}
  public float f()  {return 1;}
  public double d()  {return 1;}
  public char c()  {return 1;}

  public String str()  {return "str";}

  @Override
  public Class<? extends Annotation> annotationType() {
    return TestAnnotation.class;
  }


}
